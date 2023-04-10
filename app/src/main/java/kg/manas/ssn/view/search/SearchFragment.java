package kg.manas.ssn.view.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import kg.manas.ssn.R;

public class SearchFragment extends Fragment {
    public static final String TITLE = "SEARCH";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        TextView textView = Objects.requireNonNull(getActivity()).findViewById(R.id.title);
        textView.setText(TITLE);
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}