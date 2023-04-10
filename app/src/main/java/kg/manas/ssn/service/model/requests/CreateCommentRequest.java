package kg.manas.ssn.service.model.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class CreateCommentRequest implements Serializable {
    @SerializedName("postId")
    @Expose
    private Integer postId;

    @SerializedName("targetId")
    @Expose
    private Integer targetId;

    @SerializedName("body")
    @Expose
    private String body;
}
