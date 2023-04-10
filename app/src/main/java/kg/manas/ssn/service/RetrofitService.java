package kg.manas.ssn.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kg.manas.ssn.service.model.responses.LoginResponse;
import kg.manas.ssn.utils.interfaces.OnRefreshTokenExpired;
import kg.manas.ssn.utils.interfaces.OnSynchronized;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final String baseUrl = "https://ssn-manas.herokuapp.com/api/";
    private static boolean isRefreshCall = false;
    private static OnRefreshTokenExpired onRefreshTokenExpired = null;
    private static final List<OnSynchronized> observers = new ArrayList<>();
    private static final TokenInterceptor tokenInterceptor = new TokenInterceptor();
    private static final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static final OkHttpClient httpClient = new OkHttpClient().newBuilder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    public static Retrofit getRetrofit() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.S")
                .create();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
    }

    public static ApiService getApiService() {
        return getRetrofit().create(ApiService.class);
    }

    public static void refreshToken(String refresh) {
        RetrofitService.getApiService().refreshToken(refresh).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginResponse> call, retrofit2.@NotNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    AppPreferences.setAccessToken(response.body().jwtToken);
                    AppPreferences.setAccessTokenExpired(response.body().jwtExpires);
                    AppPreferences.setRefreshToken(response.body().refreshToken);
                    AppPreferences.setRefreshTokenExpired(response.body().refreshExpires);
                    AppPreferences.setUserName(response.body().username);
                } else {
                    if (onRefreshTokenExpired != null)
                        onRefreshTokenExpired.expired();
                }
                if (isRefreshCall) isRefreshCall = false;
            }

            @Override
            public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                if (isRefreshCall) isRefreshCall = false;
            }
        });
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static void setOnRefreshTokenExpired(OnRefreshTokenExpired onRefreshTokenExpired) {
        RetrofitService.onRefreshTokenExpired = onRefreshTokenExpired;
    }

    public static OnRefreshTokenExpired getOnRefreshTokenExpired() {
        return onRefreshTokenExpired;
    }

    public static List<OnSynchronized> getSyncObservable() {
        return observers;
    }

    public static void removeOnSynchronized(OnSynchronized onSynchronized) {
        observers.remove(onSynchronized);
    }

    public static void addOnSynchronized(OnSynchronized onSynchronized) {
        observers.add(onSynchronized);
    }

    public static boolean isIsRefreshCall() {
        return isRefreshCall;
    }

    public static void setIsRefreshCall(boolean isRefreshCall) {
        RetrofitService.isRefreshCall = isRefreshCall;
    }

    static class TokenInterceptor implements Interceptor {
        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request newRequest = chain.request()
                    .newBuilder()
                    .header("Authorization", "bearer " + AppPreferences.getAccessToken())
                    .build();
            Response response = chain.proceed(newRequest);
            if (response.code() == 401 && !isIsRefreshCall()){
                refreshToken(AppPreferences.getRefreshToken());
                isRefreshCall = true;
            }
            return response;
        }
    }

    public static void synchronizeAccount() {
        RetrofitService.getApiService().syncAccount().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, retrofit2.@NotNull Response<ResponseBody> response) {
                for (OnSynchronized onSynchronized : observers)
                    onSynchronized.synchronize(0);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                for (OnSynchronized onSynchronized : observers)
                    onSynchronized.synchronize(1);
                Log.i("SSN_SYNCHRONIZE_ERROR", t.getLocalizedMessage());
            }
        });
    }
}
