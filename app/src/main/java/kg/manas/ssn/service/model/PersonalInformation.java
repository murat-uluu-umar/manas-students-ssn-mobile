package kg.manas.ssn.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class PersonalInformation implements Serializable
{
    @SerializedName("studentNumber")
    @Expose
    private String studentNumber;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("birthdate")
    @Expose
    private String birthdate;
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("faculty")
    @Expose
    private String faculty;
    @SerializedName("profilePictureUrl")
    @Expose
    private String profileImageUrl;
    private final static long serialVersionUID = 308233030790590003L;

}