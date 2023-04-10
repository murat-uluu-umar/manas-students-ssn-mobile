package kg.manas.ssn.view.registration.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import kg.manas.ssn.R;
import kg.manas.ssn.utils.RegexUtils;

public class RegistrationViewModel extends AndroidViewModel {
    public MutableLiveData<String> studentNumber = new MutableLiveData<>();
    public MutableLiveData<String> obisPassword = new MutableLiveData<>();
    public MutableLiveData<String> userName = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public boolean useObisPassword = true;
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> confirmPassword = new MutableLiveData<>();
    private Application application;


    public MutableLiveData<String> getStudNumberError() {
        return studNumberError;
    }

    // Errors
    private MutableLiveData<String> studNumberError = new MutableLiveData<>();

    public MutableLiveData<String> getObisPasswordError() {
        return obisPasswordError;
    }

    private MutableLiveData<String> obisPasswordError = new MutableLiveData<>();

    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        this.application = application;

    }

    public boolean isStudentNumberValid() {
        if (studentNumber.getValue() == null ||
                !RegexUtils.isValidStudentNumber(studentNumber.getValue())) {
            studNumberError.setValue(application.getResources().getString(R.string.inValidStudentNumber));
            return false;
        }
        return true;
    }

    public boolean isObisPasswordValid() {
        boolean isTrue = true;
        if (obisPassword.getValue() == null) {
            obisPasswordError.setValue(application.getResources().getString(R.string.short_pass_err));
            isTrue = false;
        } else if (obisPassword.getValue().length() < 8) {
            obisPasswordError.setValue(application.getResources().getString(R.string.short_pass_err));
            isTrue = false;

        }
        return isTrue;

    }

    public boolean isStudentValid() {
        boolean isTrue = isStudentNumberValid() && isObisPasswordValid();

        return isTrue;
    }

    public MutableLiveData<String> passwordError = new MutableLiveData<>();

    public boolean isPasswordValid() {

        if (password.getValue() == null) {
            passwordError.setValue(application.getResources().getString(R.string.short_pass_err));
            return false;
        } else if (password.getValue().isEmpty() || password.getValue().isEmpty() || password.getValue().length() < 8) {
            passwordError.setValue(application.getResources().getString(R.string.short_pass_err));
            return false;
        }
        passwordError.setValue(null);
        return true;
    }

    public MutableLiveData<String> confirmPasswordError = new MutableLiveData<>();

    public boolean isConfirmPasswordValid() {
        if (confirmPassword.getValue() == null) {
            confirmPasswordError.setValue(application.getResources().getString(R.string.short_pass_err));
            return false;
        } else if (confirmPassword.getValue().isEmpty() || (confirmPassword.getValue().isEmpty())) {
            confirmPasswordError.setValue(application.getResources().getString(R.string.short_pass_err));
            return false;
        } else if (!confirmPassword.getValue().equals(password.getValue())) {
            confirmPasswordError.setValue(application.getResources().getString(R.string.didntMatch));
            return false;

        } else if (confirmPassword.getValue().length() > 7 & (confirmPassword.getValue().equals(password.getValue()) & isPasswordValid())) {
            confirmPasswordError.setValue(null);
            return true;
        }

        return false;
    }

    public String emailError;

    public boolean isEmailValid() {
        if (email.getValue() == null) {
            emailError = application.getResources().getString(R.string.inValidEmail);
            return false;
        } else if (email.getValue().isEmpty()) {
            emailError = application.getResources().getString(R.string.inValidEmail);
            return false;
        }
        emailError = null;
        return true;

    }

    public String userNameError;

    public boolean isValidUserName() {
        boolean isTrue = true;
        if (userName.getValue() == null) {
            userNameError = application.getResources().getString(R.string.inValidUserName);
            return false;
        }
        else if (email.getValue().isEmpty()) {
            emailError = application.getResources().getString(R.string.inValidEmail);
            return false;
        }
        emailError = null;


        return true;
    }

    public boolean isUseObisPassword() {
        return useObisPassword;
    }


}
