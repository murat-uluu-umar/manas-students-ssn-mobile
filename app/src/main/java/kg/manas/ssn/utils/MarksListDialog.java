package kg.manas.ssn.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kg.manas.ssn.R;
import kg.manas.ssn.adapters.MarksListAdapter;
import kg.manas.ssn.service.model.Mark;

public class MarksListDialog extends DialogFragment{
    private List<Mark> marks = new ArrayList<>();
    private String title;

    @BindView(R.id.lessonTitleMarksItem)
    TextView titleView;
    @BindView(R.id.recyclerViewMarksItem)
    RecyclerView recyclerView;

    public MarksListDialog(List<Mark> marks, String title) {
        this.marks.addAll(marks);
        this.title = title;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marks_list, container, false);
        ButterKnife.bind(this,view);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
        setCancelable(true);
        titleView.setText(title);
        MarksListAdapter adapter = new MarksListAdapter(marks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public int show(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        if (getDialog() != null) getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.show(transaction, tag);
    }
}
