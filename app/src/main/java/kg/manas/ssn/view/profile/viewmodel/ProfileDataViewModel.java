package kg.manas.ssn.view.profile.viewmodel;

import android.app.Activity;
import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kg.manas.ssn.service.AppPreferences;
import kg.manas.ssn.service.RetrofitService;
import kg.manas.ssn.service.model.LessonAndMarks;
import kg.manas.ssn.service.model.PersonalInformation;
import kg.manas.ssn.utils.ImageSaver;
import kg.manas.ssn.utils.LoadingDialog;
import kg.manas.ssn.utils.interfaces.OnSynchronized;
import kg.manas.ssn.view.profile.ProfileFragment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfileDataViewModel extends ViewModel implements OnSynchronized {

    private LoadingDialog dialog;
    private ObservableField<PersonalInformation> personalInformation = new ObservableField<>();
    private MutableLiveData<List<LessonAndMarks>> lessonsAndMarks = new MutableLiveData<>(new ArrayList<>());

    public void init(Activity activity) {
        dialog = new LoadingDialog(activity);
        dialog.show();
        PersonalInformation personalInformationPref = AppPreferences.loadPersonalInformation();
        if (personalInformationPref == null) {
            loadPersonalInformationFromNetwork();
        } else {
            personalInformation.set(personalInformationPref);
            dialog.dismiss();
        }
        List<LessonAndMarks> lessonsAndMarksPref = AppPreferences.loadLessonsAndMarks();
        if (lessonsAndMarksPref.isEmpty()) {
            loadLessonsAndMarksFromNetwork();
        } else {
            getLessonsAndMarks().setValue(lessonsAndMarksPref);
            dialog.dismiss();
        }
        RetrofitService.addOnSynchronized(this);
    }

    public void loadPersonalInformationFromNetwork() {
        RetrofitService.getApiService().personalInformation().enqueue(new Callback<PersonalInformation>() {
            @Override
            public void onResponse(@NotNull Call<PersonalInformation> call, @NotNull Response<PersonalInformation> response) {
                if (response.isSuccessful()) {
                    personalInformation.set(response.body());
                    AppPreferences.savePersonalInformation(response.body());
                } else {
                    personalInformation.set(personalInformation.get());
                }
            }
            @Override
            public void onFailure(@NotNull Call<PersonalInformation> call, @NotNull Throwable t) {
                personalInformation.set(personalInformation.get());
            }
        });
    }

    public void loadLessonsAndMarksFromNetwork() {
        RetrofitService.getApiService().lessonsAndMarks()
                .enqueue(new Callback<List<LessonAndMarks>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<LessonAndMarks>> call, retrofit2.@NotNull Response<List<LessonAndMarks>> response) {
                        if (response.isSuccessful()) {
                            getLessonsAndMarks().setValue(response.body());
                            AppPreferences.saveLessonsAndMarks(response.body());
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<LessonAndMarks>> call, @NotNull Throwable t) {
                        dialog.dismiss();
                        Log.i("SSN_ERROR", t.getMessage());
                    }
                });
    }

    @Override
    public void synchronize(int responseCode) {
        if (responseCode == 0){
            new ImageSaver(AppPreferences.context)
                    .setFileName(ProfileFragment.PROFILE_IMAGE_NAME)
                    .setDirectoryName(ProfileFragment.PATH)
                    .deleteFile();
            loadPersonalInformationFromNetwork();
            loadLessonsAndMarksFromNetwork();
        }
    }
}
