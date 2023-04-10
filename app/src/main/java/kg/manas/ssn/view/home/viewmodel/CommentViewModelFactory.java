package kg.manas.ssn.view.home.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CommentViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;

    public CommentViewModelFactory(@NonNull Application application, int postId) {
        mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CommentsViewModel(mApplication);
    }
}

