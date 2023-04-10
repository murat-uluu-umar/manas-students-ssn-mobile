package kg.manas.ssn.view.post;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class PostCreateViewModel extends AndroidViewModel {
    public MutableLiveData<String> contentText = new MutableLiveData<>("");
    public int maxLetter;
    public String newPostImagePath;

    public PostCreateViewModel(@NonNull Application application) {
        super(application);
    }
}
