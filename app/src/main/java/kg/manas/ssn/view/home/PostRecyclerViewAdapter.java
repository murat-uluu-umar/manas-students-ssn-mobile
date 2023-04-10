package kg.manas.ssn.view.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kg.manas.ssn.R;
import kg.manas.ssn.service.model.Post;
import kg.manas.ssn.utils.interfaces.ILoadMore;
import kg.manas.ssn.utils.interfaces.RecyclerViewEventListeners;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostViewHolder> implements Observer<List<Post>> {
    public List<Post> postInformation;
    boolean isLoading;
    private Context context;
    int lastVisibleItem, totalItemCount;
    int visibleThreshold = 5;
    ILoadMore loadMore;
    private final int VIEW_TYPE = 0;
    RecyclerViewEventListeners recyclerViewEventListeners;

    PostRecyclerViewAdapter(RecyclerView recyclerView, RecyclerViewEventListeners recyclerViewEventListeners, Context context) {
        this.recyclerViewEventListeners = recyclerViewEventListeners;
        this.context = context;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                assert linearLayoutManager != null;
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (loadMore != null) loadMore.onLoadMore();
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_TYPE_LOADING = 1;
        return postInformation.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view, parent, false);
            return new PostViewHolder(layoutView, recyclerViewEventListeners);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_layout, parent, false);
            return new PostViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostViewHolder holder, int position) {

    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public void onBindViewHolder(@NotNull PostViewHolder holder, int position, @NotNull List<Object> payloads) {
        if (holder.isLoad) {
            holder.progressBar.setIndeterminate(true);
        } else {
            holder.likesNum.setText(String.valueOf(postInformation.get(position).likesCount));
            holder.commentsNum.setText(String.valueOf(postInformation.get(position).commentsCount));
            holder.postOwnerName.setText(postInformation.get(position).username);
            holder.contentText.setText(postInformation.get(position).body);
            if (postInformation.get(position).isCurrentUserLiked) {
                holder.likeButton.setImageResource(R.drawable.ic_pressed_like);
            }
            if (postInformation.get(position).pictures.isEmpty())
                holder.postPhoto.setVisibility(View.GONE);
            else {
                holder.postPhoto.setVisibility(View.VISIBLE);
                holder.postPhoto.setImageList(postInformation.get(position).getSlideModelList());
            }
            if (postInformation.get(position).userPictureUrl != null && !postInformation.get(position).userPictureUrl.isEmpty())
                Glide.with(context)
                        .load(postInformation.get(position).userPictureUrl)
                        .placeholder(R.drawable.ic_userphoto)
                        .into(holder.postOwnerPhoto);
            else holder.postOwnerPhoto.setImageResource(R.drawable.ic_userphoto);
        }
        if (!payloads.isEmpty())
            if (payloads.contains("preLike"))
                updateLike(holder, position);
            else if (payloads.contains("preUnlike"))
                updateUnlike(holder, position);
            else super.onBindViewHolder(holder, position, payloads);
    }

    private void updateUnlike(PostViewHolder holder, int position) {
        holder.likeButton.setImageResource(R.drawable.ic_unpressed_like_button);
        holder.likesNum.setText(String.valueOf(postInformation.get(position).likesCount - 1));
        postInformation.get(position).likesCount -= 1;
    }

    private void updateLike(PostViewHolder holder, int position) {
        holder.likeButton.setImageResource(R.drawable.ic_pressed_like);
        holder.likesNum.setText(String.valueOf(postInformation.get(position).likesCount + 1));
        postInformation.get(position).likesCount += 1;
    }

    @Override
    public int getItemCount() {
        return postInformation.size();
    }

    @Override
    public void onChanged(List<Post> posts) {
        postInformation = posts;
        notifyDataSetChanged();
    }
}


