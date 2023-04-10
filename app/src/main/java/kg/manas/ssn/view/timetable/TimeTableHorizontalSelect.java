package kg.manas.ssn.view.timetable;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import kg.manas.ssn.R;
import kg.manas.ssn.view.ActivityMain;

public class TimeTableHorizontalSelect extends Fragment {
    @BindView(R.id.fab_rotate_to_vertical)
    FloatingActionButton actionButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timetable_layout_horizontal,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ActivityMain activityMain = (ActivityMain) ActivityMain.Context;
        assert activityMain != null;
        activityMain.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}
