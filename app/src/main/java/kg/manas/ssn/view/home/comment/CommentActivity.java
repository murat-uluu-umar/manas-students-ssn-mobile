package kg.manas.ssn.view.home.comment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import kg.manas.ssn.R;
import kg.manas.ssn.databinding.CommentActivityBinding;
import kg.manas.ssn.service.RetrofitService;
import kg.manas.ssn.service.model.Comment;
import kg.manas.ssn.service.model.Post;
import kg.manas.ssn.service.model.requests.CreateCommentRequest;
import kg.manas.ssn.service.model.responses.CommentResponse;
import kg.manas.ssn.utils.MediaUtils;
import kg.manas.ssn.utils.interfaces.IReplyListeners;
import kg.manas.ssn.view.home.viewmodel.CommentViewModelFactory;
import kg.manas.ssn.view.home.viewmodel.CommentsViewModel;
import kg.manas.ssn.view.home.viewmodel.PostViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CommentActivity extends AppCompatActivity implements IReplyListeners {
    ImageView backImageView;
    CommentsRecyclerAdapter commentsAdapter;
    CommentActivityBinding binding;
    Post postInformation = new Post();
    Intent intent;
    CommentsViewModel viewModel;
    int position = 0;

    LinearLayout replyAddressShower;
    TextView repliedCommentOwnerShower;
    ImageView cancelReplyButton;
    EditText commentEditText;
    Button addCommentButton;
    RecyclerView recyclerView;

    private Pair<Comment, String> repliedCommentAddress = new Pair<>(null, "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.comment_activity);
        replyAddressShower = findViewById(R.id.replyLinear);
        repliedCommentOwnerShower = findViewById(R.id.replyAddressShower);
        cancelReplyButton = findViewById(R.id.cancelReplyButton);
        commentEditText = findViewById(R.id.commentEditText);
        addCommentButton = findViewById(R.id.addCommentButton);
        initListeners();
        intent = getIntent();
        initCommentsPostData();
        viewModel = new ViewModelProvider(this, new CommentViewModelFactory(this.getApplication(), postInformation.id)).get(CommentsViewModel.class);
        backImageView = findViewById(R.id.backCommentActivity);
        backImageView.setOnClickListener((l) -> onBackPressed());
        recyclerView = findViewById(R.id.comments_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsAdapter = new CommentsRecyclerAdapter(recyclerView, this, postInformation);
        viewModel.comments.observeForever(commentsAdapter);
        recyclerView.setAdapter(commentsAdapter);
        viewModel.loadCommentsFromNetwork(postInformation.id);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipe_refresh_comments);
        refreshLayout.setOnRefreshListener(() -> {
            viewModel.loadCommentsFromNetworkWithObserver(integer -> {
                if (integer == 1)
                    Snackbar.make(this, binding.getRoot(), "Synchronization successful!", Snackbar.LENGTH_SHORT).show();
                else if (integer == 2)
                    Snackbar.make(this, binding.getRoot(), "Synchronization failed!", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.RED).show();
                refreshLayout.setRefreshing(false);
            }, postInformation.getId());
            new ViewModelProvider(this).get(PostViewModel.class)
                    .commentsCount(postInformation.id, integer -> {
                        postInformation.commentsCount = integer;
                        commentsAdapter.notifyDataSetChanged();
                    });
        });
    }

    private void initListeners() {
        addCommentButton.setOnClickListener(v -> {
            if ((repliedCommentAddress.second.isEmpty() && repliedCommentAddress.first == null) && replyAddressShower.getVisibility() == GONE) {
                if (!commentEditText.getText().toString().isEmpty()) {
                    String newComment = commentEditText.getText().toString();
                    CreateCommentRequest request = new CreateCommentRequest();
                    request.setBody(newComment);
                    request.setTargetId(null);
                    request.setPostId(postInformation.id);
                    RetrofitService.getApiService().createComment(request)
                            .enqueue(new Callback<CommentResponse>() {
                                @Override
                                public void onResponse(@NotNull Call<CommentResponse> call, @NotNull Response<CommentResponse> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Комментарий успешно добавлен!", Toast.LENGTH_LONG).show();
                                        new ViewModelProvider(CommentActivity.this).get(CommentsViewModel.class).loadCommentsFromNetwork(postInformation.id);
                                        if (commentsAdapter.getItemCount() != 0) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getApplication()) {
                                                @Override
                                                protected int getVerticalSnapPreference() {
                                                    return LinearSmoothScroller.SNAP_TO_START;
                                                }
                                            };
                                            smoothScroller.setTargetPosition(commentsAdapter.getItemCount() - 1);
                                            assert linearLayoutManager != null;
                                            linearLayoutManager.startSmoothScroll(smoothScroller);
                                            new ViewModelProvider(CommentActivity.this).get(PostViewModel.class)
                                                    .commentsCount(postInformation.id, integer -> postInformation.commentsCount = integer);
                                        }
                                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.toggleSoftInputFromWindow(commentEditText.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                    } else
                                        Toast.makeText(getApplicationContext(), "Не удалось добавить комментарий!", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(@NotNull Call<CommentResponse> call, @NotNull Throwable t) {
                                    MediaUtils.errorDialog(getApplicationContext(), "Oops!", Objects.requireNonNull(t.getLocalizedMessage())
                                            .concat("\n\nMaybe no network connection or problem of the server")).show();
                                }
                            });
                    commentEditText.setText("");
                    repliedCommentAddress = new Pair<>(null, "");
                    replyAddressShower.setVisibility(View.GONE);
                    repliedCommentOwnerShower.clearComposingText();
                }
            } else if ((!repliedCommentAddress.second.isEmpty() && repliedCommentAddress.first != null) && replyAddressShower.getVisibility() == VISIBLE) {
                if (!commentEditText.getText().toString().isEmpty()) {
                    String newComment = commentEditText.getText().toString();
                    Comment comment = repliedCommentAddress.first;
                    CreateCommentRequest request = new CreateCommentRequest();
                    request.setBody(newComment);
                    request.setTargetId(comment.isReplyComment() ? comment.targetId : comment.getId());
                    request.setPostId(null);
                    RetrofitService.getApiService().createComment(request)
                            .enqueue(new Callback<CommentResponse>() {
                                @Override
                                public void onResponse(@NotNull Call<CommentResponse> call, @NotNull Response<CommentResponse> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Комментарий успешно добавлен!", Toast.LENGTH_LONG).show();
                                        assert response.body() != null;
                                        new ViewModelProvider(CommentActivity.this).get(CommentsViewModel.class).loadCommentsFromNetworkReply(postInformation.id, response.body().getId(), recyclerView);
                                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.toggleSoftInputFromWindow(commentEditText.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                        new ViewModelProvider(CommentActivity.this).get(PostViewModel.class)
                                                .commentsCount(postInformation.id, integer -> postInformation.commentsCount = integer);
                                    } else
                                        Toast.makeText(getApplicationContext(), "Не удалось добавить комментарий!", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(@NotNull Call<CommentResponse> call, @NotNull Throwable t) {
                                    MediaUtils.errorDialog(getApplicationContext(), "Oops!", Objects.requireNonNull(t.getLocalizedMessage())
                                            .concat("\n\nMaybe no network connection or problem of the server")).show();
                                }
                            });
                    commentEditText.setText("");
                    repliedCommentAddress = new Pair<>(null, "");
                    replyAddressShower.setVisibility(View.GONE);
                    repliedCommentOwnerShower.clearComposingText();
                    if (comment.isReplyComment)
                        commentsAdapter.bindReplies(comment.parentPos);
                }
            }
        });
        cancelReplyButton.setOnClickListener(v -> {
            repliedCommentAddress = new Pair<>(null, "");
            repliedCommentOwnerShower.setText("");
            replyAddressShower.setVisibility(View.GONE);
        });
    }

    private void initCommentsPostData() {
        postInformation = Objects.requireNonNull(new ViewModelProvider(this).get(PostViewModel.class)
                .postInformationList.getValue()).get(intent.getIntExtra("position", 0));
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void replyToCommentClicked(int position) {
        this.position = position;
        replyAddressShower.setVisibility(VISIBLE);
        repliedCommentOwnerShower.setText("Reply to: @" + Objects.requireNonNull(viewModel.comments.getValue()).get(position).getUsername());
        commentEditText.setText("");
        commentEditText.setText("@" + viewModel.comments.getValue().get(position).getUsername() + " ");
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(commentEditText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        commentEditText.requestFocus();
        commentEditText.setSelection(viewModel.comments.getValue().get(position).getUsername().length() + 2);
        repliedCommentAddress = new Pair<>(viewModel.comments.getValue().get(position), "url of comment address");
    }
}
