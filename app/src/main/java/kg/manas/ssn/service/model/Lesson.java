package kg.manas.ssn.service.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import kg.manas.ssn.R;
import kg.manas.ssn.service.AppPreferences;
import kg.manas.ssn.utils.BusinessUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Lesson implements Serializable
{
    @SerializedName("weekday")
    @Expose
    public Weekday weekday;
    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("timeFrom")
    @Expose
    public String timeFrom;
    @SerializedName("timeTo")
    @Expose
    public String timeTo;
    @SerializedName("teacher")
    @Expose
    public String teacher;
    @SerializedName("classroom")
    @Expose
    public String classroom;
    @SerializedName("isMandatory")
    @Expose
    public Boolean isMandatory;
    private final static long serialVersionUID = -1701336791338848634L;

    private boolean isOpen = false;

    public String getInfo() {
        return String.format("%s\n%s",teacher, classroom);
    }

    public String getName(){
        return String.format("%s | %s",code,name);
    }

    public DateTime getStartLesson(){
        return BusinessUtils.stringToDateTime("HH:mm",timeFrom);
    }

    public DateTime getEndLesson(){
        return BusinessUtils.stringToDateTime("HH:mm",timeTo);
    }

    public String getTime(){
        return String.format("%s - %s",timeFrom,timeTo);
    }

    public int getColor(){
        if (isMandatory) return AppPreferences.context.getResources().getColor(R.color.green);
        else return AppPreferences.context.getResources().getColor(R.color.yellow);
    }
}
