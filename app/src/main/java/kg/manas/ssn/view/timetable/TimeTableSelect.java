package kg.manas.ssn.view.timetable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import kg.manas.ssn.R;
import kg.manas.ssn.adapters.TimeTablePageAdapter;
import kg.manas.ssn.view.timetable.viewmodel.TimeTableViewModel;
import lombok.Getter;

@Getter
@SuppressLint("NonConstantResourceId")
public class TimeTableSelect extends Fragment{
    public static final String TITLE = "TIMETABLE";

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.rightArrowTimeTable)
    ImageButton rightButton;
    @BindView(R.id.leftArrowTimeTable)
    ImageButton leftButton;
    @BindView(R.id.fab_rotate)
    FloatingActionButton fabRot;
    @BindView(R.id.toolbarTimeTable)
    Toolbar toolbar;

    private TimeTablePageAdapter timeTablePageAdapter;
    private boolean isHorizontal = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.timetable_layout, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        TextView textView = Objects.requireNonNull(getActivity()).findViewById(R.id.title);
        textView.setText(TITLE);
        timeTablePageAdapter = new TimeTablePageAdapter(getChildFragmentManager());
        viewPager.setAdapter(timeTablePageAdapter);
        viewPager.setSaveEnabled(false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                TimeTableViewModel.getInstance().getTimeTable().setCurrentWeekDay(position);
                spinner.setSelection(position, true);
                if (position == 0)
                    leftButton.setVisibility(View.INVISIBLE);
                else leftButton.setVisibility(View.VISIBLE);
                if (position == 4)
                    rightButton.setVisibility(View.INVISIBLE);
                else rightButton.setVisibility(View.VISIBLE);

            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        viewPager.setCurrentItem(timeTablePageAdapter.getTimeTable().getCurrentWeekDay(),true);
        if (viewPager.getCurrentItem() == 0) leftButton.setVisibility(View.INVISIBLE);
        if (viewPager.getCurrentItem() == 4) rightButton.setVisibility(View.INVISIBLE);
        spinner.setSelection(timeTablePageAdapter.getTimeTable().getCurrentWeekDay(), true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                viewPager.setCurrentItem(i, true);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        leftButton.setOnClickListener(view -> viewPager.setCurrentItem(viewPager.getCurrentItem()-1));
        rightButton.setOnClickListener(view -> viewPager.setCurrentItem(viewPager.getCurrentItem()+1));
        fabRot.setOnClickListener( v -> {
            Snackbar.make(getContext(),getView(),"Coming soon",Snackbar.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("TOAST","DESTROY!");
    }
}
