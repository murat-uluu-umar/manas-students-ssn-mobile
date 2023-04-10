package kg.manas.ssn.view.timetable;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kg.manas.ssn.R;
import kg.manas.ssn.adapters.LessonsAdapter;
import kg.manas.ssn.service.model.TimeTable;

@SuppressLint("NonConstantResourceId")
public class TimeTableFragment extends Fragment {
    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private LessonsAdapter lessonsAdapter;

    public TimeTableFragment(){}

    public TimeTableFragment(TimeTable timeTable,int i){
        lessonsAdapter = new LessonsAdapter(timeTable.getLessonsList().get(i));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.timetable_recycler_layout,container,false);
        ButterKnife.bind(this,v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(lessonsAdapter);
    }
}
