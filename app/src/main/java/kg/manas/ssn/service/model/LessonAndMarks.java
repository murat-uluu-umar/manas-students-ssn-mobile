package kg.manas.ssn.service.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class LessonAndMarks implements Serializable {
    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("theoryAbsent")
    @Expose
    public Double theoryAbsent;
    @SerializedName("theoryAbsentPercentage")
    @Expose
    public Double theoryAbsentPercentage;
    @SerializedName("practiceAbsent")
    @Expose
    public Double practiceAbsent;
    @SerializedName("practiceAbsentPercentage")
    @Expose
    public Double practiceAbsentPercentage;
    @SerializedName("averageMark")
    @Expose
    public String averageMark;
    @SerializedName("marks")
    @Expose
    public List<Mark> marks = new ArrayList<>();
    private final static long serialVersionUID = 2629034274524358299L;
}