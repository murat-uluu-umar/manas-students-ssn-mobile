package kg.manas.ssn.service.model.responses;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class LoginResponse implements Serializable {
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("userId")
    @Expose
    public String userId;
    @SerializedName("jwtToken")
    @Expose
    public String jwtToken;
    @SerializedName("refreshToken")
    @Expose
    public String refreshToken;
    @SerializedName("refreshExpires")
    @Expose
    public String refreshExpires;
    @SerializedName("jwtExpires")
    @Expose
    public String jwtExpires;
    private final static long serialVersionUID = 4832956275908107737L;

}