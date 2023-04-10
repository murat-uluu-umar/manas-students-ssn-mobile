package kg.manas.ssn.service.model.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class LikeRequest implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;
}
