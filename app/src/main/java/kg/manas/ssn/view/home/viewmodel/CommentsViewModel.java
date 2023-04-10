package kg.manas.ssn.view.home.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kg.manas.ssn.service.AppPreferences;
import kg.manas.ssn.service.RetrofitService;
import kg.manas.ssn.service.model.Comment;
import kg.manas.ssn.service.model.Post;
import kg.manas.ssn.view.ActivityMain;
import kg.manas.ssn.view.home.comment.CommentsRecyclerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsViewModel extends AndroidViewModel {
    public MutableLiveData<List<Comment>> comments = new MutableLiveData<>(new ArrayList<>());

    public CommentsViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadCommentsFromNetwork(int postId) {
        RetrofitService.getApiService().showComments(postId)
                .enqueue(new Callback<List<Comment>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Comment>> call, @NotNull Response<List<Comment>> response) {
                        if (response.isSuccessful()) {
                            comments.setValue(response.body());
                            assert comments.getValue() != null;
                            if (comments.getValue().size() != 0) {
                                List<Post> posts = AppPreferences.loadPosts();
                                for (Post post : posts) {
                                    if (post.id == postId) {
                                        post.setCommentsCount(comments.getValue().size());
                                    }
                                }
                                AppPreferences.savePosts(posts);
                            }
                        } else {
                            Toast.makeText(ActivityMain.Context, response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Comment>> call, @NotNull Throwable t) {
                        Log.i("SSN_ERROR", t.getMessage());
                    }
                });
    }

    public void loadCommentsFromNetworkReply(int postId, int commentId, RecyclerView recyclerView) {
        RetrofitService.getApiService().showComments(postId)
                .enqueue(new Callback<List<Comment>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Comment>> call, @NotNull Response<List<Comment>> response) {
                        if (response.isSuccessful()) {
                            comments.setValue(response.body());
                            assert comments.getValue() != null;
                            if (comments.getValue().size() != 0) {
                                List<Post> posts = AppPreferences.loadPosts();
                                for (Post post : posts) {
                                    if (post.id == postId) {
                                        post.setCommentsCount(comments.getValue().size());
                                    }
                                }
                                AppPreferences.savePosts(posts);
                            }
                            CommentsRecyclerAdapter recyclerAdapter = (CommentsRecyclerAdapter) recyclerView.getAdapter();
                            assert response.body() != null && recyclerAdapter != null;
                            for (int i = 0; i < response.body().size(); i++) {
                                if (response.body().get(i) != null)
                                    for (Comment comment : response.body().get(i).getReplies()) {
                                        if (comment.getId() == commentId) {
                                            recyclerAdapter.bindReplies(i);
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getApplication()) {
                                                @Override protected int getVerticalSnapPreference() {
                                                    return LinearSmoothScroller.SNAP_TO_START;
                                                }
                                            };
                                            smoothScroller.setTargetPosition(i + (response.body().get(i).getReplies().size() - 1));
                                            assert linearLayoutManager != null;
                                            linearLayoutManager.startSmoothScroll(smoothScroller);
                                            return;
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(ActivityMain.Context, response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Comment>> call, @NotNull Throwable t) {
                        Log.i("SSN_ERROR", t.getMessage());
                    }
                });
    }

    public void loadCommentsFromNetworkWithObserver(Observer<Integer> observer, int postId) {
        RetrofitService.getApiService().showComments(postId)
                .enqueue(new Callback<List<Comment>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Comment>> call, @NotNull Response<List<Comment>> response) {
                        if (response.isSuccessful()) {
                            comments.setValue(response.body());
                            assert comments.getValue() != null;
                            if (comments.getValue().size() != 0) {
                                List<Post> posts = AppPreferences.loadPosts();
                                for (Post post : posts) {
                                    if (post.id == postId) {
                                        post.setCommentsCount(comments.getValue().size());
                                    }
                                }
                                AppPreferences.savePosts(posts);
                            }
                            observer.onChanged(1);
                        } else {
                            Toast.makeText(ActivityMain.Context, response.message(), Toast.LENGTH_SHORT).show();
                            observer.onChanged(2);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Comment>> call, @NotNull Throwable t) {
                        Log.i("SSN_ERROR", t.getMessage());
                        observer.onChanged(2);
                    }
                });
    }
}

