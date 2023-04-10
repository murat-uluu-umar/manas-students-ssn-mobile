package kg.manas.ssn.view.timetable.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kg.manas.ssn.service.AppPreferences;
import kg.manas.ssn.service.RetrofitService;
import kg.manas.ssn.service.model.Lesson;
import kg.manas.ssn.service.model.TimeTable;
import kg.manas.ssn.utils.interfaces.OnSynchronized;
import kg.manas.ssn.view.ActivityMain;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Setter
@Getter
public class TimeTableViewModel extends ViewModel implements OnSynchronized {
    private TimeTable timeTable;
    private MutableLiveData<List<Lesson>> timeTableData = new MutableLiveData<>();

    public void init() {
        timeTable = new TimeTable();
        List<Lesson> lessons = AppPreferences.loadLessons();
        if (lessons.isEmpty())
            loadLessonsFromNetwork();
        else {
            timeTableData.setValue(lessons);
        }
        RetrofitService.addOnSynchronized(this);
    }

    public static TimeTableViewModel getInstance() {
        return new ViewModelProvider((ViewModelStoreOwner) ActivityMain.Context).get(TimeTableViewModel.class);
    }

    private void loadLessonsFromNetwork() {
        RetrofitService.getApiService().lessons().enqueue(new Callback<List<Lesson>>() {
            @Override
            public void onResponse(@NotNull Call<List<Lesson>> call, @NotNull Response<List<Lesson>> response) {
                if (response.isSuccessful()) {
                    timeTableData.setValue(response.body());
                    AppPreferences.saveLessons(response.body());
                } else {
                    Log.i("SNN_ERROR_LESSONS: ", response.message());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Lesson>> call, @NotNull Throwable t) {
                Log.i("SNN_ERROR_LESSONS: ", t.getMessage());
            }
        });
    }

    @Override
    public void synchronize(int responseCode) {
        if (responseCode == 0)
            loadLessonsFromNetwork();
    }
}
