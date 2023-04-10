package kg.manas.ssn.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import kg.manas.ssn.R;
import kg.manas.ssn.service.AppPreferences;
import kg.manas.ssn.service.RetrofitService;
import kg.manas.ssn.service.model.Lesson;
import kg.manas.ssn.service.model.TimeTable;
import kg.manas.ssn.utils.ImageSaver;
import kg.manas.ssn.utils.MediaUtils;
import kg.manas.ssn.utils.interfaces.OnRefreshTokenExpired;
import kg.manas.ssn.view.home.HomeFragment;
import kg.manas.ssn.view.login.ActivityLogin;
import kg.manas.ssn.view.post.PostCreateFragment;
import kg.manas.ssn.view.profile.ProfileFragment;
import kg.manas.ssn.view.profile.viewmodel.ProfileDataViewModel;
import kg.manas.ssn.view.search.SearchFragment;
import kg.manas.ssn.view.timetable.TimeTableSelect;
import kg.manas.ssn.view.timetable.viewmodel.TimeTableViewModel;
import lombok.Getter;
import lombok.Setter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Getter
@Setter
@SuppressLint("StaticFieldLeak")
public class ActivityMain extends AppCompatActivity implements OnRefreshTokenExpired {

    public static Context Context;

    private HomeFragment homeFragment;
    private PostCreateFragment postCreateFragment;
    private ProfileFragment profileFragment;
    private SearchFragment searchFragment;
    private TimeTableSelect timeTableSelect;

    private FragmentTransaction transaction = null;

    @BindView(R.id.bottomNavigationView)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.appBar)
    Toolbar appBarLayout;
    @BindView(R.id.refreshActivityMain)
    public ImageView refreshButton;
    @BindView(R.id.backActivityMain)
    public ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Context = this;
        TimeTableViewModel.getInstance().init();
        RetrofitService.setOnRefreshTokenExpired(this);
        init();
        initialize();
    }

    public void init() {
        homeFragment = new HomeFragment();
        postCreateFragment = new PostCreateFragment();
        profileFragment = new ProfileFragment();
        searchFragment = new SearchFragment();
        timeTableSelect = new TimeTableSelect();
        TimeTable timeTable = TimeTableViewModel.getInstance().getTimeTable();
        TimeTableViewModel.getInstance().getTimeTableData().observe(this, lessons -> {
            timeTable.clear();
            for (Lesson lesson : lessons)
                timeTable.getLessonsList().get(lesson.weekday.value - 1).add(lesson);
            timeTable.sort();
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void initialize() {
        navigateTo(homeFragment, false);
        backButton.setOnClickListener(view -> {
            backButton.startAnimation(AnimationUtils.loadAnimation(ActivityMain.this,R.anim.log_out_animation));
            MediaUtils.logoutDialog((dialogInterface, i) -> {
                RetrofitService.getApiService().revokeToken(AppPreferences.getRefreshToken()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) AppPreferences.clearAll();
                        new ImageSaver(AppPreferences.context)
                                .setFileName(ProfileFragment.PROFILE_IMAGE_NAME)
                                .setDirectoryName(ProfileFragment.PATH)
                                .deleteFile();
                    }
                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {}
                });
                startActivity(new Intent(ActivityMain.this,ActivityLogin.class));
                finish();
            }).show();
        });
        refreshButton.setOnClickListener(view -> {
            refreshButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.synchronize_animation));
            RetrofitService.synchronizeAccount();
        });
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    navigateTo(homeFragment, false);
                    return true;
                case R.id.newPost:
                    navigateTo(postCreateFragment, false);
                    return true;
                case R.id.profile:
                    navigateTo(profileFragment, false);
                    return true;
                case R.id.search:
                    navigateTo(searchFragment, false);
                    return true;
                case R.id.timeTable:
                    navigateTo(timeTableSelect, false);
                    return true;
            }
            return false;
        });
    }

    public void navigateTo(Fragment fragment, boolean addToBackStack) {
        transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.view_pager, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void expired() {
        AppPreferences.setIsLoggedIn(false);
        AppPreferences.clearAll();
        new ImageSaver(AppPreferences.context)
                .setFileName(ProfileFragment.PROFILE_IMAGE_NAME)
                .setDirectoryName(ProfileFragment.PATH)
                .deleteFile();
        startActivity(new Intent(this,ActivityLogin.class));
        finish();
    }
}