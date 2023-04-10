package kg.manas.ssn.view.splash;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import kg.manas.ssn.R;
import kg.manas.ssn.service.AppPreferences;
import kg.manas.ssn.service.RetrofitService;
import kg.manas.ssn.service.model.responses.LoginResponse;
import kg.manas.ssn.utils.MediaUtils;
import kg.manas.ssn.view.ActivityMain;
import kg.manas.ssn.view.login.ActivityLogin;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NonConstantResourceId")
public class ActivitySplash extends AppCompatActivity{
    @BindView(R.id.splashImage)
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        imageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce_anim));
        RetrofitService.getApiService().wakeUp().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) { }
            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {}
        });
        if (AppPreferences.isLoggedIn())
            checkRefresh();
        else{
            startActivity(new Intent(this, ActivityLogin.class));
            finish();
        }
    }

    public void checkRefresh() {
        RetrofitService.setIsRefreshCall(true);
        RetrofitService.getApiService().refreshToken(AppPreferences.getRefreshToken()).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    AppPreferences.setAccessToken(response.body().jwtToken);
                    AppPreferences.setRefreshToken(response.body().refreshToken);
                    AppPreferences.setAccessTokenExpired(response.body().jwtExpires);
                    AppPreferences.setRefreshTokenExpired(response.body().refreshExpires);
                    startActivity(new Intent(ActivitySplash.this, ActivityMain.class));
                } else {
                    startActivity(new Intent(ActivitySplash.this, ActivityLogin.class));
                }
                finish();
                RetrofitService.setIsRefreshCall(false);
            }

            @Override
            public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                RetrofitService.setIsRefreshCall(false);
                DateTime now = DateTime.now();
                boolean before = now.isBefore(AppPreferences.getAccessTokenExpired());
                if (before) {
                    startActivity(new Intent(ActivitySplash.this, ActivityMain.class));
                    finish();
                } else {
                    AlertDialog dialog = MediaUtils.errorDialog(ActivitySplash.this, "Oops!", "No internet connection!");
                    dialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "Retry", (dialogInterface, i) -> checkRefresh());
                    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "", (dialogInterface, i) -> {
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        });
    }
}
