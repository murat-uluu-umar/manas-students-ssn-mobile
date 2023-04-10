package kg.manas.ssn.view.home;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;

import de.hdodenhof.circleimageview.CircleImageView;
import kg.manas.ssn.R;
import kg.manas.ssn.utils.interfaces.RecyclerViewEventListeners;

public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView contentText;
    TextView likesNum;
    TextView commentsNum;
    TextView postOwnerName;
    CircleImageView postOwnerPhoto;
    ImageSlider postPhoto;
    ImageButton likeButton;
    ImageButton commentButton;

    RecyclerViewEventListeners listeners;
    boolean isLoad=false;

    ProgressBar progressBar;

    public PostViewHolder(@NonNull View itemView,RecyclerViewEventListeners listeners) {
        super(itemView);
        contentText = itemView.findViewById(R.id.postContentText);
        likesNum = itemView.findViewById(R.id.likesNum);
        commentsNum = itemView.findViewById(R.id.commentsNum);
        postOwnerPhoto = itemView.findViewById(R.id.postOwnerPhoto);
        postOwnerName = itemView.findViewById(R.id.postOwnerName);
        postPhoto = itemView.findViewById(R.id.postImage);
        likeButton = itemView.findViewById(R.id.likeButton);
        commentButton = itemView.findViewById(R.id.commentButton);
        likeButton.setOnClickListener(this);
        commentButton.setOnClickListener(this::onCommentClick);
        this.listeners = listeners;
    }
    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        isLoad = true;
        progressBar = itemView.findViewById(R.id.progressbar);
    }
    @Override
    public void onClick(View v) {
        listeners.onLikeClicked(getAbsoluteAdapterPosition());
    }
    public void onCommentClick(View v){
         listeners.onCommentClicked(getAbsoluteAdapterPosition());
    }
}
