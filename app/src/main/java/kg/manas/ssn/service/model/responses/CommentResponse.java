package kg.manas.ssn.service.model.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import lombok.Data;

@Data
public class CommentResponse implements Serializable {
    @SerializedName("id")
    @Expose
    int id;
}