package kg.manas.ssn.view.home.viewmodel;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kg.manas.ssn.service.AppPreferences;
import kg.manas.ssn.service.RetrofitService;
import kg.manas.ssn.service.model.Post;
import kg.manas.ssn.view.ActivityMain;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostViewModel extends AndroidViewModel {
    public MutableLiveData<List<Post>> postInformationList = new MutableLiveData<>(new ArrayList<>());
    Resources resources;

    public int getMaxDataCount() {
        return maxDataCount;
    }

    public void setMaxDataCount(int maxDataCount) {
        this.maxDataCount = maxDataCount;
    }

    private int maxDataCount = 40;

    public PostViewModel(@NonNull Application application) {
        super(application);
        resources = application.getResources();
        if (RetrofitService.isNetworkConnected(application))
            loadMoreData();
        else postInformationList.setValue(AppPreferences.loadPosts());
        if (postInformationList.getValue().size()==0)
            postInformationList.setValue(AppPreferences.loadPosts());
    }

    public void loadNewData(Observer<Integer> isEnd) {
        isEnd.onChanged(0);
        RetrofitService.getApiService().posts()
                .enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Post>> call, @NotNull Response<List<Post>> response) {
                        if (response.isSuccessful()) {
                            postInformationList.setValue(response.body());
                            AppPreferences.savePosts(postInformationList.getValue());
                            isEnd.onChanged(1);
                        } else
                            Toast.makeText(ActivityMain.Context, response.message(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Post>> call, @NotNull Throwable t) {
                        isEnd.onChanged(2);
                        Log.i("SSN_POST", t.getMessage());
                    }
                });
    }

    public void loadMoreData() {
        RetrofitService.getApiService().posts()
                .enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Post>> call, @NotNull Response<List<Post>> response) {
                        if (response.isSuccessful()) {
                            postInformationList.setValue(response.body());
                            AppPreferences.savePosts(postInformationList.getValue());
                        } else
                            Toast.makeText(ActivityMain.Context, response.message(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Post>> call, @NotNull Throwable t) {
                        Log.i("SSN_POST", t.getMessage());
                    }
                });
    }

    public void commentsCount(int id, Observer<Integer> observer) {
        RetrofitService.getApiService().posts()
                .enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Post>> call, @NotNull Response<List<Post>> response) {
                        if (response.isSuccessful()) {
                            postInformationList.setValue(response.body());
                            AppPreferences.savePosts(postInformationList.getValue());
                            assert response.body() != null;
                            for (Post post : response.body())
                                if (post.id == id) observer.onChanged(post.commentsCount);
                        } else
                            Toast.makeText(ActivityMain.Context, response.message(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Post>> call, @NotNull Throwable t) {
                        Log.i("SSN_POST", t.getMessage());
                    }
                });
    }
}

