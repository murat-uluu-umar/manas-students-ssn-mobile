package kg.manas.ssn.view.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import kg.manas.ssn.R;
import kg.manas.ssn.databinding.FragmentHomeBinding;
import kg.manas.ssn.service.ApiService;
import kg.manas.ssn.service.AppPreferences;
import kg.manas.ssn.service.RetrofitService;
import kg.manas.ssn.service.model.Post;
import kg.manas.ssn.service.model.requests.LikeRequest;
import kg.manas.ssn.utils.LoadingDialog;
import kg.manas.ssn.utils.MediaUtils;
import kg.manas.ssn.utils.interfaces.RecyclerViewEventListeners;
import kg.manas.ssn.view.ActivityMain;
import kg.manas.ssn.view.home.comment.CommentActivity;
import kg.manas.ssn.view.home.viewmodel.PostViewModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements RecyclerViewEventListeners {

    FragmentHomeBinding binding;
    PostViewModel viewModel;
    public static final String TITLE = "HOME";
    PostRecyclerViewAdapter postAdapter;
    ApiService apiService = RetrofitService.getApiService();
    public ActivityMain activityMain;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = Objects.requireNonNull(getActivity()).findViewById(R.id.title);
        textView.setText(TITLE);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);

        View view = binding.getRoot();
        RecyclerView recyclerView = view.findViewById(R.id.post_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(() -> viewModel.loadNewData(integer -> {
            if (integer == 1)
                Snackbar.make(requireActivity(), binding.getRoot(), "Synchronization successful!", Snackbar.LENGTH_SHORT).show();
            else if (integer == 2)
                Snackbar.make(requireActivity(), binding.getRoot(), "Synchronization failed!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.RED).show();
        }));
        postAdapter = new PostRecyclerViewAdapter(recyclerView, this,getActivity());
        viewModel.postInformationList.observeForever(postAdapter);
        recyclerView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();

//        postAdapter.setLoadMore(() -> {
//            int index = viewModel.postInformationList.size();
//            // time stopper
//            viewModel.postInformationList.add(null);
//            postAdapter.notifyItemInserted(viewModel.postInformationList.size() - 1);
//            new Handler().postDelayed(() -> {
//                viewModel.postInformationList.remove(viewModel.postInformationList.size() - 1);
//                postAdapter.notifyItemRemoved(viewModel.postInformationList.size());
//                viewModel.loadMoreDate();
//                postAdapter.setLoaded();
//                postAdapter.notifyDataSetChanged();
//            }, 5000);
//
//            int end = viewModel.postInformationList.size();
//            if (index == end) {
//                Toast.makeText(getContext(), "Data set is not Loaded!", Toast.LENGTH_LONG).show();
//            }
//
//        });

        if (postAdapter.loadMore != null) postAdapter.loadMore.onLoadMore();
        postAdapter.isLoading = true;
        refresh();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onLikeClicked(int position) {
        int id = Objects.requireNonNull(viewModel.postInformationList.getValue()).get(position).id;
        LikeRequest likeRequest = new LikeRequest();
        likeRequest.setId(id);
        Call<ResponseBody> call = apiService.likePost(likeRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if(! response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Не удалось поставить/убрать лайк!", Toast.LENGTH_LONG).show();
                }
                else {
                    updatePosts();
                    if (viewModel.postInformationList.getValue().get(position).isCurrentUserLiked) {
                        viewModel.postInformationList.getValue().get(position).isCurrentUserLiked = false;
                        postAdapter.notifyItemChanged(position, "preUnlike");
                    } else {
                        viewModel.postInformationList.getValue().get(position).isCurrentUserLiked = true;
                        postAdapter.notifyItemChanged(position, "preLike");
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                MediaUtils.errorDialog(getContext(), "Oops!", Objects.requireNonNull(t.getLocalizedMessage())
                        .concat("\n\nMaybe no network connection or problem of the server")).show();
            }
        });


    }

    @Override
    public void onCommentClicked(int position) {
        LoadingDialog dialog = new LoadingDialog(getActivity());
        dialog.show();
        Intent intent = new Intent(getActivity(), CommentActivity.class);
        intent.putExtra("position",position);
        startActivity(intent);
        dialog.dismiss();
    }

/*    public void deletePost(int position) {
        LoadingDialog dialog = new LoadingDialog(getActivity());
        dialog.show();
        int id = viewModel.postInformationList.get(position).postId;
        DeletePostRequest deletePostRequest = new DeletePostRequest();
        deletePostRequest.setId(id);
        Call<ResponseBody> call = apiService.deletePost(deletePostRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Пост успешно удален!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity(), ActivityMain.class));
                } else {
                    MediaUtils.errorDialog(getContext(), "Внимание!", "Не удалось удалить пост!").show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                dialog.dismiss();
                MediaUtils.errorDialog(getContext(), "Oops!", Objects.requireNonNull(t.getLocalizedMessage())
                        .concat("\n\nMaybe no network connection or problem of the server")).show();
            }
        });
    }*/

    private void updatePosts() {
        RetrofitService.getApiService().posts()
                .enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Post>> call, @NotNull Response<List<Post>> response) {
                        if (response.isSuccessful()) {
                            AppPreferences.savePosts(response.body());
                            AppPreferences.loadPosts();
                        }
                    }
                    @Override
                    public void onFailure(@NotNull Call<List<Post>> call, @NotNull Throwable t) { }
                });
    }
    public void refresh(){
        if(RetrofitService.isNetworkConnected(requireActivity())){
            viewModel.loadMoreData();
        }
    }
}