package kg.manas.ssn.view.login.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import kg.manas.ssn.R;
import kg.manas.ssn.utils.RegexUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginViewModel extends AndroidViewModel {

    private MutableLiveData<String> userName = new MutableLiveData<>();
    private MutableLiveData<String> studNumber = new MutableLiveData<>();
    private MutableLiveData<String> password =  new MutableLiveData<>();
    private Application application;

    private MutableLiveData<String> studNumberError = new MutableLiveData<>();
    private MutableLiveData<String> passwordError = new MutableLiveData<>();
    private MutableLiveData<String> userNameError = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }
    public boolean isStudentNumberValid() {
        if (studNumber.getValue() == null ||
                !RegexUtils.isValidStudentNumber(studNumber.getValue())) {
            studNumberError.setValue(application.getResources().getString(R.string.inValidStudentNumber));
            return false;
        }
        return true;
    }
    public boolean isUserNameValid() {
        if(userName.getValue()==null){
            userNameError.setValue(application.getResources().getString(R.string.username_empty_err));
            return false;
        }
        return true;
    }
    public boolean isPasswordValid(){

        if(password.getValue()==null){
            passwordError.setValue(application.getResources().getString(R.string.short_pass_err));
            return false;
        }
        else if(password.getValue().isEmpty() || password.getValue().isEmpty() || password.getValue().length() < 8){
            passwordError.setValue(application.getResources().getString(R.string.short_pass_err));
            return false;
        }
        passwordError.setValue(null);
        return true;
    }
    public boolean isStudentValid(){
        boolean isTrue1 = isStudentNumberValid() && isPasswordValid();
        boolean isTrue2 = isUserNameValid() && isPasswordValid();

        return isTrue1 || isTrue2;
    }
}
