package kg.manas.ssn.view.registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.Objects;

import kg.manas.ssn.R;
import kg.manas.ssn.databinding.FragmentRegistrationPage1Binding;
import kg.manas.ssn.service.model.requests.CheckUserRequest;
import kg.manas.ssn.service.model.responses.CheckUserResponse;
import kg.manas.ssn.service.ApiService;
import kg.manas.ssn.service.RetrofitService;
import kg.manas.ssn.utils.LoadingDialog;
import kg.manas.ssn.utils.MediaUtils;
import kg.manas.ssn.view.ActivityMain;
import kg.manas.ssn.view.NavigationHost;

import kg.manas.ssn.view.login.ActivityLogin;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import kg.manas.ssn.view.registration.viewmodel.RegistrationViewModel;


public class RegistrationFragmentPage1 extends Fragment {
    private RegistrationViewModel registrationViewModel;
    private FragmentRegistrationPage1Binding binding;
    ApiService apiService = RetrofitService.getApiService();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRegistrationPage1Binding.inflate(inflater,container,false);
        registrationViewModel = ViewModelProviders.of(this).get(RegistrationViewModel.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setRegistrationViewModel(registrationViewModel);

        binding.backBt.setOnClickListener((l)->{
            startActivity(new Intent(getActivity(), ActivityLogin.class));
            getActivity().finish();
        });
        binding.idNextbt1.setOnClickListener((l)->{
            if(registrationViewModel.isStudentValid())
                 studentExist();
            else {
                if(registrationViewModel.studentNumber.getValue()==null)binding.usernameTextInput.setError(getResources().getString(R.string.studenNumEmptyErr));
                if(registrationViewModel.obisPassword.getValue()==null) binding.passwordTextInput.setError(getResources().getString(R.string.short_pass_err));
            }
        });

        return binding.getRoot();

    }

    private void studentExist() {

        String studentNumber = binding.usernameEditText.getText().toString();
        String studentPassword = binding.passwordEditText.getText().toString();

        CheckUserRequest checkUserRequest = new CheckUserRequest(studentNumber, studentPassword);
        Call<CheckUserResponse> call = apiService.isStudentExist(checkUserRequest);
        LoadingDialog dialog = new LoadingDialog(getActivity());
        dialog.show();
        call.enqueue(new Callback<CheckUserResponse>() {
            @Override
            public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                if(response.isSuccessful()) {
                    ((NavigationHost) getActivity()).navigateTo(new RegistrationFragmentPage2(studentNumber, studentPassword), true);
                }
                else {
                    if(response.body().getMessage() != null)
                        MediaUtils.errorDialog(getContext(),"Внимание!",response.body().getMessage()).show();
                    binding.usernameEditText.setError(getResources().getString(R.string.stNumPassInvalid));
                    binding.passwordEditText.setError(getResources().getString(R.string.stNumPassInvalid));
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                MediaUtils.errorDialog(getContext(),"Внимание!",t.getMessage()).show();
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        registrationViewModel.studentNumber.observe(getActivity(), s -> {
           if(!registrationViewModel.isStudentNumberValid() || TextUtils.isEmpty(Objects.requireNonNull(s)))
               binding.usernameTextInput.setError(registrationViewModel.getStudNumberError().getValue());
           else
               binding.usernameTextInput.setError(null);
        });
        registrationViewModel.obisPassword.observe(getActivity(), s -> {
            if(!registrationViewModel.isObisPasswordValid()||TextUtils.isEmpty(Objects.requireNonNull(s)) )
                binding.passwordTextInput.setError(registrationViewModel.getObisPasswordError().getValue());
            else
                binding.passwordTextInput.setError(null);
        });
    }

}