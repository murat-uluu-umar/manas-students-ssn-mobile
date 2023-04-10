package kg.manas.ssn.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Comment implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("body")
    @Expose
    private String body;

    @SerializedName("likesCount")
    @Expose
    private int likesCount;

    @SerializedName("commentsCount")
    @Expose
    private int commentsCount;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("replies")
    @Expose
    private List<Comment> replies;

    @SerializedName("profilePictureUrl")
    @Expose
    public String profilePhotoUrl;

    public boolean isReplyComment;
    public int notShownRepliesNum;
    public int targetId = 0;
    public int parentPos = -1;
    public boolean showReplies = false;

    public Comment(boolean isReplyComment, String commentOwnerName,String commentContentText, int notShownRepliesNum) {
        this.isReplyComment = isReplyComment;
        this.username = commentOwnerName;
        this.notShownRepliesNum = notShownRepliesNum;
        this.body = commentContentText;
    }

}
