package kg.manas.ssn.service.model;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Post implements Serializable
{

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("body")
    @Expose
    public String body;
    @SerializedName("tags")
    @Expose
    public List<String> tags = null;
    @SerializedName("pictures")
    @Expose
    public List<String> pictures = null;
    @SerializedName("userPictureUrl")
    @Expose
    public String userPictureUrl;
    @SerializedName("isCurrentUserLiked")
    @Expose
    public boolean isCurrentUserLiked;
    @SerializedName("likesCount")
    @Expose
    public int likesCount;
    @SerializedName("commentsCount")
    @Expose
    public int commentsCount;
    @SerializedName("updatedAt")
    @Expose
    public String updatedAt;

    public List<SlideModel> getSlideModelList(){
        List<SlideModel> slideModelList = new ArrayList<>();
        for (String url : pictures)
            slideModelList.add(new SlideModel(url, ScaleTypes.CENTER_INSIDE));
        return slideModelList;
    }
}