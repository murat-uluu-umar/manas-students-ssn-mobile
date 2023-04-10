package kg.manas.ssn.view.registration;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Objects;

import kg.manas.ssn.databinding.FragmentRegistrationPage3Binding;
import kg.manas.ssn.service.ApiService;
import kg.manas.ssn.service.AppPreferences;
import kg.manas.ssn.service.RetrofitService;
import kg.manas.ssn.service.model.responses.VerifyEmailResponse;
import kg.manas.ssn.utils.BusinessUtils;
import kg.manas.ssn.utils.LoadingDialog;
import kg.manas.ssn.utils.MediaUtils;
import kg.manas.ssn.view.login.ActivityLogin;
import kg.manas.ssn.view.registration.viewmodel.RegistrationViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class RegistrationFragmentPage3 extends Fragment {
    private FragmentRegistrationPage3Binding binding;
    private Bitmap bitmap;

    ApiService apiService = RetrofitService.getApiService();
    String stNumber, stPassword, username, email, password;
    String token;
    Uri filePath = null;

    public RegistrationFragmentPage3(String stNumber, String stPassword, String username, String email, String password) {
        this.stNumber = stNumber;
        this.stPassword = stPassword;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding  = FragmentRegistrationPage3Binding.inflate(inflater,container,false);
        RegistrationViewModel registrationViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setRegViewModel(registrationViewModel);
        bitmap = ((BitmapDrawable)binding.profileImage.getDrawable()).getBitmap();
        binding.backBt.setOnClickListener((l)-> getParentFragmentManager().popBackStack());
        binding.uploadPhotoTextClick.setOnClickListener(v -> Dexter.withContext(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent,"Please select Image"),1);
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) { }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) { }
                }).check());
        binding.btSkip.setOnClickListener(l->{
            try {
                register();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        return binding.getRoot();
    }

    private void register() throws URISyntaxException {
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody requestStNum = RequestBody.create(stNumber,mediaType);
        RequestBody requestStPass = RequestBody.create(stPassword,mediaType);
        RequestBody requestUsername = RequestBody.create(username,mediaType);
        RequestBody requestEmail = RequestBody.create(email,mediaType);
        RequestBody requestPassword = RequestBody.create(password,mediaType);
        MultipartBody.Part filePart = null;
        if (filePath != null) {
            File file = new File(Objects.requireNonNull(BusinessUtils.getPath(getContext(), filePath)));
            filePart = MultipartBody.Part.createFormData("ProfilePicture", file.getName(), RequestBody.create(file, MediaType.parse("application/octet-stream")));
        }
        Call<ResponseBody> call = apiService.registerUser(requestStNum,
                requestStPass,
                requestUsername,
                requestEmail,
                requestPassword,
                filePart);
        LoadingDialog dialog = new LoadingDialog(getActivity());
        dialog.show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Поздравляем! Вы успешно зарегистрировались!", Toast.LENGTH_LONG).show();
                    AppPreferences.setUserName(username);
                    AppPreferences.setEmail(email);
                    AppPreferences.setStudentNumber(stNumber);
                    startActivity(new Intent(getActivity(),ActivityLogin.class));
                    getActivity().finish();
                } else {
                    MediaUtils.errorDialog(getContext(),"Внимание","Введены неправильные данные или вы уже зарегистрированы!").show();
                }
                dialog.dismiss();
            }
            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                dialog.dismiss();
                MediaUtils.errorDialog(getContext(),"Oops!",t.getLocalizedMessage()
                        .concat("\n\nMaybe no network connection or problem of the server")).show();
            }
        });
    }

    private void verifyEmail() {
        token = AppPreferences.getRefreshToken();
        Call<VerifyEmailResponse> call = apiService.verifyEmail(email, token);
        call.enqueue(new Callback<VerifyEmailResponse>() {
            @Override
            public void onResponse(Call<VerifyEmailResponse> call, Response<VerifyEmailResponse> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Код подтверждения отправлен на почту: " + email, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Ошибка! Не удалось отправить код подтверждения на почту: " + email, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<VerifyEmailResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resendConfirmEmail() {
        Call<ResponseBody> call = apiService.resendConfirm(email);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Confirmation code resent to your email: " + email, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getActivity(), "Error! Couldn't resend confirmation code to email: " + email, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(requestCode==1 && resultCode == RESULT_OK)
        {
            try {
                InputStream inputStream = Objects.requireNonNull(getActivity()).getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
                bitmap = BitmapFactory.decodeStream(inputStream);
                binding.profileImage.setImageBitmap(bitmap);
                filePath = data.getData();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}