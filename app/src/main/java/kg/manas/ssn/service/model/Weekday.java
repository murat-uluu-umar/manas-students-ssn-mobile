package kg.manas.ssn.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Weekday implements Serializable
{
    @SerializedName("value")
    @Expose
    public int value;
    @SerializedName("name")
    @Expose
    public String name;
}