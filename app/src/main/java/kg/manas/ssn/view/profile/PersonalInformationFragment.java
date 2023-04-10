package kg.manas.ssn.view.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import kg.manas.ssn.R;
import kg.manas.ssn.databinding.FragmentPersonalInformationBinding;
import kg.manas.ssn.view.profile.viewmodel.PageViewModel;
import kg.manas.ssn.view.profile.viewmodel.ProfileDataViewModel;

public class PersonalInformationFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static PersonalInformationFragment newInstance(int index) {
        PersonalInformationFragment fragment = new PersonalInformationFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PageViewModel pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProfileDataViewModel profileDataViewModel = new ViewModelProvider(requireActivity()).get(ProfileDataViewModel.class);
        FragmentPersonalInformationBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_information, container, false);
        binding.setViewModel(profileDataViewModel);
        return binding.getRoot();
    }
}