package kg.manas.ssn.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import kg.manas.ssn.service.model.TimeTable;
import kg.manas.ssn.view.timetable.TimeTableFragment;
import kg.manas.ssn.view.timetable.viewmodel.TimeTableViewModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeTablePageAdapter extends FragmentStatePagerAdapter {
    private TimeTable timeTable;
    private List<Fragment> days = new ArrayList<>();

    public TimeTablePageAdapter(@NonNull FragmentManager fm) {
        super(fm);
        timeTable = TimeTableViewModel.getInstance().getTimeTable();
        for (int i = 0; i < timeTable.getLessonsList().size(); i++) {
            days.add( new TimeTableFragment(timeTable,i));
        }

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return days.get(position);
    }

    @Override
    public int getCount() {
        return days.size();
    }
}
