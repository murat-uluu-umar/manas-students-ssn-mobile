package kg.manas.ssn.service.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class TimeTable {
    public static final int DAYS = 5;
    private List<List<Lesson>> lessonsList = new ArrayList<>();
    private int currentWeekDay;

    public TimeTable() {
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (currentDay != Calendar.SATURDAY && currentDay != Calendar.SUNDAY)
            currentWeekDay = currentDay - 2;
        else currentWeekDay = 0;
        for (int i = 0;i < DAYS; i++)
            getLessonsList().add(new ArrayList<>());
    }

    public List<Lesson> getCurrent() {
        return lessonsList.get(currentWeekDay);
    }

    public void clear(){
        getLessonsList().clear();
        for (int i = 0;i < DAYS; i++)
            getLessonsList().add(new ArrayList<>());
    }

    public void sort() {
        for (List<Lesson> list : lessonsList) {
            Collections.sort(list, (lesson, t1) -> lesson.getStartLesson().compareTo(t1.getStartLesson()));
        }
    }
}
