package kg.manas.ssn.view.home.comment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kg.manas.ssn.R;
import kg.manas.ssn.service.AppPreferences;
import kg.manas.ssn.service.model.Comment;
import kg.manas.ssn.service.model.Post;
import kg.manas.ssn.utils.BusinessUtils;
import kg.manas.ssn.utils.interfaces.ILoadMoreComment;
import kg.manas.ssn.view.home.viewmodel.CommentViewModelFactory;
import kg.manas.ssn.view.home.viewmodel.CommentsViewModel;

import static android.view.View.GONE;
import static androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Observer<List<Comment>> {
    public Observer<Integer> observer = null;
    List<Comment> commentList = new ArrayList<>();
    RecyclerView recyclerView;
    CommentsViewModel viewModel;
    CommentActivity activity;
    Context context;
    Post postInformation;
    int lastVisibleItem;
    int visibleThreshold = 5;
    boolean isLoading;

    private ILoadMoreComment loadMoreComment;

    private final int COMMENT_VIEW_TYPE = 1, LOADING_VIEW_TYPE = 2, COMMENT_ITEM_POST_VIEW_TYPE = 3;

    public CommentsRecyclerAdapter(RecyclerView recyclerView, CommentActivity activity, Post postInformation) {
        this.recyclerView = recyclerView;
        this.postInformation = postInformation;
        this.activity = activity;
        viewModel = new ViewModelProvider((ViewModelStoreOwner) recyclerView.getContext(), new CommentViewModelFactory(new AppPreferences().getInstance(), postInformation.id)).get(CommentsViewModel.class);
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                assert linearLayoutManager != null;
                int totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (loadMoreComment != null) loadMoreComment.loadMoreComment();
                    isLoading = true;
                }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == COMMENT_VIEW_TYPE) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_view, parent, false);
            return new CommentViewHolder(layoutView, activity);
        }
        else if (viewType == COMMENT_ITEM_POST_VIEW_TYPE){
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_post_view, parent, false);
            return new CommentItemPostViewHolder(layoutView);
        }
        else {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_layout, parent, false);
            return new LoadingCommentViewHolder(layoutView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case COMMENT_VIEW_TYPE:
                CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
                bindCommentViewHolder(commentViewHolder, position);
                break;
            case LOADING_VIEW_TYPE:
                LoadingCommentViewHolder loadingCommentViewHolder = (LoadingCommentViewHolder) holder;
                bindLoadingCommentViewHolder(loadingCommentViewHolder);
                break;
            case COMMENT_ITEM_POST_VIEW_TYPE:
                CommentItemPostViewHolder commentItemPostViewHolder = (CommentItemPostViewHolder) holder;
                bindCommentPost(commentItemPostViewHolder);
                break;
        }
    }

    private void bindLoadingCommentViewHolder(LoadingCommentViewHolder loadingCommentViewHolder) {
        loadingCommentViewHolder.progressBar.setIndeterminate(true);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (commentList.get(position) == null)
            return COMMENT_ITEM_POST_VIEW_TYPE;
        else
            return COMMENT_VIEW_TYPE;
    }

    @SuppressLint("SetTextI18n")
    private void bindCommentViewHolder(CommentViewHolder commentViewHolder, int position) {
        Comment commentModel = commentList.get(position);
        commentViewHolder.commentOwnerName.setText(commentModel.getUsername());
        commentViewHolder.commentText.setText(commentModel.getBody());
        if (commentModel.getProfilePhotoUrl() != null && !commentModel.getProfilePhotoUrl().isEmpty())
            Glide.with(context).load(commentModel.getProfilePhotoUrl()).into(commentViewHolder.commentOwnerPhoto);
        else
            Glide.with(context).load(R.drawable.ic_userphoto).into(commentViewHolder.commentOwnerPhoto);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) commentViewHolder.itemView.getLayoutParams();
        if (commentModel.getReplies() != null) {
            if (commentModel.getReplies().size() != 0)
                if (!commentModel.showReplies)
                    commentViewHolder.repliesShowerNumClick.setText("Show replies (" + commentModel.getReplies().size() + ")");
                else commentViewHolder.repliesShowerNumClick.setText("Hide replies");
            else commentViewHolder.repliesShowerNumClick.setText("");
            params.setMargins(0, 0, 0, 0);
        } else {
            commentModel.isReplyComment = true;
            commentViewHolder.repliesShowerNumClick.setText("");
            params.setMargins((BusinessUtils.getDisplaySize(context).x * 10) / 100, 0, 0, 0);
        }
        commentViewHolder.repliesShowerNumClick.setOnClickListener(view -> bindReplies(position));
    }

    @Override
    public void onChanged(List<Comment> comments) {
        commentList = comments;
        if ( comments.size()!=0 && comments.get(0) != null )
            commentList.add(0,null);
        else if (comments.size()==0) commentList.add(null);
        notifyDataSetChanged();
        if (observer != null)
            observer.onChanged(0);
    }

    public void setLoadMoreComment(ILoadMoreComment loadMoreComment) {
        this.loadMoreComment = loadMoreComment;
    }

    public void bindReplies(int position) {
        Log.i("RecyclerAdapter", String.format("%d", position));
        if (Objects.requireNonNull(viewModel.comments.getValue()).get(position).getReplies().isEmpty()) {
            Toast.makeText(AppPreferences.context, "No replies", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.comments.getValue().get(position).showReplies = !viewModel.comments.getValue().get(position).showReplies;
        if (viewModel.comments.getValue().get(position).showReplies)
            viewModel.comments.getValue().addAll(position + 1, viewModel.comments.getValue().get(position).getReplies());
        else
            viewModel.comments.getValue().removeAll(viewModel.comments.getValue().get(position).getReplies());
        for ( Comment comment : viewModel.comments.getValue().get(position).getReplies()) {
            comment.targetId = viewModel.comments.getValue().get(position).getId();
            comment.parentPos = position;
            Log.i("comment.parentPos: ", String.format("%d", position));
        }
        viewModel.comments.setValue(viewModel.comments.getValue());
    }
    private void bindCommentPost(CommentItemPostViewHolder binding) {
        binding.postContentText.setText(postInformation.body);
        binding.likesNum.setText(String.valueOf(postInformation.likesCount));
        binding.commentsNum.setText(String.valueOf(postInformation.commentsCount));
        if (postInformation.userPictureUrl != null && !postInformation.userPictureUrl.isEmpty())
            Glide.with(context).load(postInformation.userPictureUrl).into(binding.postOwnerPhoto);
        else
            Glide.with(context).load(R.drawable.ic_userphoto).into(binding.postOwnerPhoto);
        binding.postOwnerName.setText(postInformation.username);
        if (postInformation.pictures.isEmpty())
            binding.postImage.setVisibility(GONE);
        else binding.postImage.setImageList(postInformation.getSlideModelList());
    }
}
