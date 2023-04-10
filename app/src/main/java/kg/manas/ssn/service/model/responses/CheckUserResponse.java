package kg.manas.ssn.service.model.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckUserResponse {

    @SerializedName("isExist")
    @Expose
    private boolean isExist;
    @SerializedName("message")
    @Expose
    private String message;


    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
