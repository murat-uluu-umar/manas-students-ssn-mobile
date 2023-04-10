package kg.manas.ssn.view.post;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kg.manas.ssn.R;
import kg.manas.ssn.databinding.FragmentPostBinding;
import kg.manas.ssn.service.ApiService;
import kg.manas.ssn.service.RetrofitService;
import kg.manas.ssn.utils.BusinessUtils;
import kg.manas.ssn.utils.LoadingDialog;
import kg.manas.ssn.utils.MediaUtils;
import kg.manas.ssn.view.ActivityMain;
import kg.manas.ssn.view.home.viewmodel.PostViewModel;
import kg.manas.ssn.view.profile.viewmodel.ProfileDataViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class PostCreateFragment extends Fragment {

    public static final String TITLE = "NEW POST";
    PostCreateViewModel viewModel;
    ProfileDataViewModel viewModel1;
    PostViewModel postViewModel;
    FragmentPostBinding binding;
    ActivityMain main;
    Uri filePath = null;

    ApiService apiService = RetrofitService.getApiService();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        TextView textView = Objects.requireNonNull(getActivity()).findViewById(R.id.title);
        textView.setText(TITLE);

        main = (ActivityMain) getActivity();
        binding = FragmentPostBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(this).get(PostCreateViewModel.class);
        viewModel1 = new ViewModelProvider(this).get(ProfileDataViewModel.class);
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        binding.setViewModel1(viewModel1);

        viewModel.maxLetter = Integer.parseInt((String) binding.remainingTextCounter.getText());
        return binding.getRoot();
    }
    @Override
    public void onStart() {
        super.onStart();
        viewModel1.init(getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        viewModel.contentText.observe(requireActivity(), s -> {
            if(!TextUtils.isEmpty(Objects.requireNonNull(s) ))
             binding.remainingTextCounter.setText(String.valueOf(viewModel.maxLetter- Objects.requireNonNull(viewModel.contentText.getValue()).length()));
            else binding.remainingTextCounter.setText(String.valueOf(viewModel.maxLetter));

        });
        binding.newPostImage.setOnClickListener(v -> Dexter.withContext(getActivity())
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

        binding.postButton.setOnClickListener(v -> {
            try {
                createPost();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(requestCode==1 && resultCode == RESULT_OK)
        {
            try {
                assert data != null;
                InputStream inputStream = Objects.requireNonNull(getActivity()).getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                binding.newPostImage.setImageBitmap(bitmap);
                filePath = data.getData();
                viewModel.newPostImagePath = filePath.toString();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private void createPost() throws URISyntaxException {

        String body = binding.contentText.getText().toString();
        List<String> tags = new ArrayList<>();
        MediaType mediaType = MediaType.parse("multipart/form-data");

        RequestBody requestBody = RequestBody.create(body, mediaType);
        List<MultipartBody.Part> filePart = new ArrayList<>();
        if(filePath != null) {
            File file = new File(Objects.requireNonNull(BusinessUtils.getPath(getContext(), filePath)));
            filePart.add(MultipartBody.Part.createFormData("Pictures", file.getName(), RequestBody.create(file, MediaType.parse("application/octet-stream"))));
        }
        Call<ResponseBody> call = apiService.createPost(
                requestBody,
                filePart,
                tags
        );

        LoadingDialog dialog = new LoadingDialog(getActivity());
        dialog.show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Пост успешно создан!", Toast.LENGTH_LONG).show();
                    main.navigateTo(main.getHomeFragment(),false);
                }
                else {
                    MediaUtils.errorDialog(getContext(), "Внимание!", "Не удалось создать пост!").show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                dialog.dismiss();
                MediaUtils.errorDialog(getContext(), "Oops!", Objects.requireNonNull(t.getLocalizedMessage())
                        .concat("\n\nMaybe no network connection or problem of the server")).show();
            }
        });
    }

}