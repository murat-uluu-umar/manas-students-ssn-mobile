package kg.manas.ssn.service.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Mark implements Serializable
{
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("point")
    @Expose
    public Integer point;
    private final static long serialVersionUID = -1804339633080913257L;

    public String getName(){
        return name.toString();
    }
    public String getPoint(){
        return point.toString();
    }
}