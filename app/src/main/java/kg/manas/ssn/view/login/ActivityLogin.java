package kg.manas.ssn.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import kg.manas.ssn.R;
import kg.manas.ssn.databinding.ActivityLoginBinding;
import kg.manas.ssn.service.ApiService;
import kg.manas.ssn.service.AppPreferences;
import kg.manas.ssn.service.RetrofitService;
import kg.manas.ssn.service.model.requests.LoginRequest;
import kg.manas.ssn.service.model.responses.LoginResponse;
import kg.manas.ssn.utils.LoadingDialog;
import kg.manas.ssn.utils.RegexUtils;
import kg.manas.ssn.view.ActivityMain;
import kg.manas.ssn.view.login.viewmodel.LoginViewModel;
import kg.manas.ssn.view.registration.ActivityRegistration;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLogin extends AppCompatActivity{
    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;
    ApiService apiService = RetrofitService.getApiService();
    String username, studentNumber, password, refreshToken, token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setLifecycleOwner(this);
        binding.setLoginViewModel(loginViewModel);
        binding.idCreateAccount.setOnClickListener((l) -> {
            startActivity(new Intent(ActivityLogin.this, ActivityRegistration.class));
            finish();
        });
        binding.idForgotPassword.setOnClickListener((l) -> {
            startActivity(new Intent(this, ActivityForgot.class));
            finish();
        });
        binding.idLoginbt.setOnClickListener((l) -> {
            if (loginViewModel.isStudentValid()) login();
            else {
                if (loginViewModel.getStudNumber() == null && loginViewModel.getUserName() == null)
                    binding.usernameEditText.setError(getResources().getString(R.string.stNumUserNEmptyErr));
                if (loginViewModel.getPassword() == null)
                    binding.passwordEditText.setError(getResources().getString(R.string.short_pass_err));
            }
        });
    }

    private void login() {
        String username_field = binding.usernameEditText.getText().toString().trim();
        username = RegexUtils.isValidStudentNumber(username_field) ? null : binding.usernameEditText.getText().toString().trim();
        studentNumber = RegexUtils.isValidStudentNumber(username_field) ? binding.usernameEditText.getText().toString().trim() : null;

        LoadingDialog dialog = new LoadingDialog(this);
        dialog.show();

        password = binding.passwordEditText.getText().toString().trim();
        Call<LoginResponse> call = apiService.userLogin(new LoginRequest(username, studentNumber, password));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    refreshToken = response.body().refreshToken;
                    AppPreferences.setRefreshToken(refreshToken);
                    token = response.body().jwtToken;
                    AppPreferences.setAccessToken(token);
                    AppPreferences.setIsLoggedIn(true);
                    boolean b = AppPreferences.isLoggedIn();
                    Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
                    startActivity(intent);
                } else{
                    binding.usernameEditText.setError(getResources().getString(R.string.stNumUserNNotMatchErr));
                    binding.passwordEditText.setError(getResources().getString(R.string.stNumUserNNotMatchErr));
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                Toast.makeText(ActivityLogin.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

    private void refreshToken() {
        Call<LoginResponse> call = apiService.refreshToken(refreshToken);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                if (response.isSuccessful())
                    Toast.makeText(ActivityLogin.this, "Токен действителен!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(ActivityLogin.this, "Ошибка при проверке действительности токена...", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                Toast.makeText(ActivityLogin.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void revokeToken() {
        Call<ResponseBody> call = apiService.revokeToken(refreshToken);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful())
                    Toast.makeText(ActivityLogin.this, "Токен успешно удален!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(ActivityLogin.this, " Ошибка при удалении токена...", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ActivityLogin.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginViewModel.getUserName().observe(ActivityLogin.this, s -> {
            if (!loginViewModel.isUserNameValid() || TextUtils.isEmpty(Objects.requireNonNull(s)))
                binding.usernameTextInput.setError(loginViewModel.getStudNumberError().getValue());
            else binding.usernameTextInput.setError(null);
        });
        loginViewModel.getPassword().observe(ActivityLogin.this, s -> {
            if (!loginViewModel.isPasswordValid() || TextUtils.isEmpty(Objects.requireNonNull(s)))
                binding.passwordTextInput.setError(loginViewModel.getPasswordError().getValue());
            else binding.passwordTextInput.setError(null);
        });
    }
}