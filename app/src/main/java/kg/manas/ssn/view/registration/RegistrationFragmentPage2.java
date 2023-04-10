package kg.manas.ssn.view.registration;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import kg.manas.ssn.databinding.FragmentRegistrationPage2Binding;
import kg.manas.ssn.view.NavigationHost;
import kg.manas.ssn.view.registration.viewmodel.RegistrationViewModel;


public class RegistrationFragmentPage2 extends Fragment {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch useNewPass;
    private RegistrationViewModel registrationViewModel;
    private FragmentRegistrationPage2Binding binding;

    String stNumber, stPassword;
    String username, email, password;

    public RegistrationFragmentPage2(String stNumber, String stPassword) {
        this.stNumber = stNumber;
        this.stPassword = stPassword;
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        binding = FragmentRegistrationPage2Binding.inflate(inflater, container, false);
        registrationViewModel = ViewModelProviders.of(this).get(RegistrationViewModel.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setRegistrationViewModel(registrationViewModel);
        useNewPass = binding.useNewPasswordCheckBox;
        useNewPass.setChecked(true);
        useNewPass.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                binding.supportLayout.setVisibility(View.VISIBLE);
                registrationViewModel.useObisPassword = false;
            } else {
                binding.supportLayout.setVisibility(View.GONE);
                registrationViewModel.useObisPassword = true;
            }
        });

        binding.backBt.setOnClickListener((l) -> {
            getFragmentManager().popBackStack();
        });
        binding.idNextbt.setOnClickListener((l) -> {
            next();
        });


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registrationViewModel.password.observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                registrationViewModel.isPasswordValid();
                binding.regPassword1TextInput.setError(registrationViewModel.passwordError.getValue());
            }
        });


    }

    public void next() {
        if (registrationViewModel.isEmailValid() & registrationViewModel.isValidUserName()) {
            binding.regUsernameTextInput.setError(null);
            binding.regEmailTextInput.setError(null);

            if (registrationViewModel.useObisPassword) {
                username = binding.regUsernameEditText.getText().toString();
                email = binding.regEmailEditTex.getText().toString();
                password = binding.regPassword1EditText.getText().toString();

                ((NavigationHost) getActivity()).navigateTo(new RegistrationFragmentPage3(stNumber, stPassword, username, email, stPassword), true);
            }
            else if (registrationViewModel.isConfirmPasswordValid()) {
                username = binding.regUsernameEditText.getText().toString();
                email = binding.regEmailEditTex.getText().toString();
                password = binding.regPassword1EditText.getText().toString();

                binding.regPassword2TextInput.setError(registrationViewModel.confirmPasswordError.getValue());
                binding.regPassword1TextInput.setError(registrationViewModel.passwordError.getValue());

                ((NavigationHost) getActivity()).navigateTo(new RegistrationFragmentPage3(stNumber, stPassword, username, email, password), true);
            } else {
                binding.regPassword2TextInput.setError(registrationViewModel.confirmPasswordError.getValue());
                binding.regPassword1TextInput.setError(registrationViewModel.passwordError.getValue());

            }

        } else {

            if (!registrationViewModel.useObisPassword) {
                registrationViewModel.isConfirmPasswordValid();

                binding.regPassword2TextInput.setError(registrationViewModel.confirmPasswordError.getValue());
                binding.regPassword1TextInput.setError(registrationViewModel.passwordError.getValue());
            }

            binding.regUsernameTextInput.setError(registrationViewModel.userNameError);
            binding.regEmailTextInput.setError(registrationViewModel.emailError);
        }
    }

}