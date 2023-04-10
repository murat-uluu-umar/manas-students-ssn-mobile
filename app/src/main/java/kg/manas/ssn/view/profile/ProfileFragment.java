package kg.manas.ssn.view.profile;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import kg.manas.ssn.R;
import kg.manas.ssn.adapters.ProfileSectionsPagerAdapter;
import kg.manas.ssn.databinding.FragmentPostBinding;
import kg.manas.ssn.databinding.FragmentProfileBinding;
import kg.manas.ssn.service.AppPreferences;
import kg.manas.ssn.service.RetrofitService;
import kg.manas.ssn.utils.ImageSaver;
import kg.manas.ssn.utils.interfaces.OnSynchronized;
import kg.manas.ssn.view.ActivityMain;
import kg.manas.ssn.view.profile.viewmodel.ProfileDataViewModel;

public class ProfileFragment extends Fragment implements OnSynchronized {
    public static final String TITLE = "PROFILE";
    public static final String PATH = "ssnMediaData";
    public static final String PROFILE_IMAGE_NAME = "profileImage.jpg";
    public ProfileDataViewModel profileDataViewModel;
    public ProfileSectionsPagerAdapter profilesectionsPagerAdapter;
    FragmentProfileBinding binding;
    FragmentPostBinding binding1;
    public ActivityMain activity;
    public Bitmap profileImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle();
        profileDataViewModel = new ViewModelProvider(requireActivity()).get(ProfileDataViewModel.class);
        profilesectionsPagerAdapter = new ProfileSectionsPagerAdapter(getActivity(), getChildFragmentManager());
        activity = (ActivityMain) ActivityMain.Context;
        activity.refreshButton.setVisibility(View.VISIBLE);
        activity.backButton.setVisibility(View.VISIBLE);
        profileImage = new ImageSaver(getContext())
                .setDirectoryName(PATH)
                .setFileName(PROFILE_IMAGE_NAME)
                .load();
        RetrofitService.addOnSynchronized(this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        binding1 = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false);
        binding.setViewModel(profileDataViewModel);
        binding1.setViewModel1(profileDataViewModel);
        binding.userPhoto.setDrawingCacheEnabled(true);
        binding1.myPhoto.setDrawingCacheEnabled(true);
        profileDataViewModel.getPersonalInformation().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                loadProfileImage();
            }
        });
        binding.viewPager.setAdapter(profilesectionsPagerAdapter);
        binding.tabs.setupWithViewPager(binding.viewPager);
        return binding.getRoot();
    }

    public void setTitle() {
        TextView textView = Objects.requireNonNull(getActivity()).findViewById(R.id.title);
        textView.setText(TITLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        profileDataViewModel.init(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.refreshButton.setVisibility(View.GONE);
        activity.backButton.setVisibility(View.GONE);
    }

    public void loadProfileImage() {
        String path = Objects.requireNonNull(profileDataViewModel.getPersonalInformation().get()).getProfileImageUrl();
        if (profileImage == null) {
            if (path == null)
                binding.userPhoto.setImageResource(R.drawable.ic_userphoto);
            else Picasso.get().load(path)
                    .error(R.drawable.ic_userphoto)
                    .into(binding.userPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = binding.userPhoto.getDrawingCache();
                            new ImageSaver(AppPreferences.context)
                                    .setDirectoryName(PATH)
                                    .setFileName(PROFILE_IMAGE_NAME)
                                    .save(bitmap);
                        }
                        @Override
                        public void onError(Exception e) {}
                    });
        } else binding.userPhoto.setImageBitmap(profileImage);
    }

    @Override
    public void synchronize(int responseCode) {
        if (responseCode == 0) {
            activity.refreshButton.clearAnimation();
            Snackbar.make(activity, binding.getRoot(), "Synchronize is successful!", Snackbar.LENGTH_SHORT).show();
        } else {
            activity.refreshButton.clearAnimation();
            Snackbar.make(activity, binding.getRoot(), "Synchronization failed!", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED).show();
        }
        LessonsMarksFragment fragment = (LessonsMarksFragment) profilesectionsPagerAdapter.getFragments()[1];
        fragment.adapter.notifyDataSetChanged();
    }
}