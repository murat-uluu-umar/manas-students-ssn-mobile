package kg.manas.ssn.view.profile;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import kg.manas.ssn.R;
import kg.manas.ssn.adapters.LessonCardRecyclerViewAdapter;
import kg.manas.ssn.databinding.FragmentLessonsMarksBinding;
import kg.manas.ssn.service.model.Mark;
import kg.manas.ssn.utils.MarksListDialog;
import kg.manas.ssn.utils.interfaces.OnLessonItemClickListener;
import kg.manas.ssn.view.profile.viewmodel.PageViewModel;
import kg.manas.ssn.view.profile.viewmodel.ProfileDataViewModel;

public class LessonsMarksFragment extends Fragment implements OnLessonItemClickListener {

    public ProfileDataViewModel profileDataViewModel;
    public LessonCardRecyclerViewAdapter adapter;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static LessonsMarksFragment newInstance(int index) {
        LessonsMarksFragment fragment = new LessonsMarksFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PageViewModel pageViewModel = new ViewModelProvider(requireActivity()).get(PageViewModel.class);
        profileDataViewModel = new ViewModelProvider(requireActivity()).get(ProfileDataViewModel.class);
        adapter = new LessonCardRecyclerViewAdapter(this);
        profileDataViewModel.getLessonsAndMarks().observeForever(adapter);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentLessonsMarksBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lessons_marks, container, false);
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onLessonItemClick(int position) {
        MarksListDialog marksListDialog = new MarksListDialog(adapter.getLessonsAndMarksList().get(position).marks
                ,adapter.getLessonsAndMarksList().get(position).code);
        marksListDialog.show(getChildFragmentManager(),"MarksDialog");
    }
}