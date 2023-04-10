package kg.manas.ssn.view.home.comment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import kg.manas.ssn.R;
import kg.manas.ssn.utils.interfaces.IReplyListeners;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    TextView commentOwnerName;
    TextView commentText;
    ImageView commentOwnerPhoto;
    TextView repliesShowerNumClick;
    TextView replyClick;
    IReplyListeners replyListeners;
    public CommentViewHolder(@NonNull View itemView,IReplyListeners replyListeners) {
        super(itemView);
        commentOwnerName = itemView.findViewById(R.id.commentOwnerName);
        commentText = itemView.findViewById(R.id.commentContentText);
        commentOwnerPhoto = itemView.findViewById(R.id.commentOwnerPhoto);
        repliesShowerNumClick = itemView.findViewById(R.id.showRepliesButton);
        replyClick = itemView.findViewById(R.id.replyButton);
        this.replyListeners = replyListeners;
        initListeners();
    }
    private void initListeners(){
        replyClick.setOnClickListener((l)-> replyListeners.replyToCommentClicked(getBindingAdapterPosition()));
    }
}
class LoadingCommentViewHolder extends RecyclerView.ViewHolder{
    boolean isLoad;
    ProgressBar progressBar;
    public LoadingCommentViewHolder(@NonNull View itemView) {
        super(itemView);
        isLoad = true;
        progressBar = itemView.findViewById(R.id.progressbar);
    }
}
@SuppressLint("NonConstantResourceId")
class CommentItemPostViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.postContentText)
    TextView postContentText;
    @BindView(R.id.likesNum)
    TextView likesNum;
    @BindView(R.id.commentsNum)
    TextView commentsNum;
    @BindView(R.id.postOwnerPhoto)
    CircleImageView postOwnerPhoto;
    @BindView(R.id.postOwnerName)
    TextView postOwnerName;
    @BindView(R.id.postImage)
    ImageSlider postImage;

    public CommentItemPostViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
