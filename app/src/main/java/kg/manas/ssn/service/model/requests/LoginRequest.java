package kg.manas.ssn.service.model.requests;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class LoginRequest implements Serializable {

    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("studentNumber")
    @Expose
    public String studentNumber;
    @SerializedName("password")
    @Expose
    public String password;
    private final static long serialVersionUID = 8955823188614462750L;

    public LoginRequest(String username, String studentNumber, String password) {
        this.username = username;
        this.studentNumber = studentNumber;
        this.password = password;
    }
}