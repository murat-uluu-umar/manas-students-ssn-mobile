package kg.manas.ssn.service;

import java.util.List;

import kg.manas.ssn.service.model.Comment;
import kg.manas.ssn.service.model.Lesson;
import kg.manas.ssn.service.model.LessonAndMarks;
import kg.manas.ssn.service.model.PersonalInformation;
import kg.manas.ssn.service.model.Post;
import kg.manas.ssn.service.model.requests.CheckUserRequest;
import kg.manas.ssn.service.model.requests.CreateCommentRequest;
import kg.manas.ssn.service.model.requests.DeletePostRequest;
import kg.manas.ssn.service.model.requests.LikeRequest;
import kg.manas.ssn.service.model.requests.LoginRequest;
import kg.manas.ssn.service.model.responses.CheckUserResponse;
import kg.manas.ssn.service.model.responses.CommentResponse;
import kg.manas.ssn.service.model.responses.LoginResponse;
import kg.manas.ssn.service.model.responses.VerifyEmailResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("authenticate")
    Call<LoginResponse> userLogin(@Body LoginRequest request);

    @POST("revoke-token")
    Call<ResponseBody> revokeToken(@Query("token") String token);

    @POST("check-student")
    Call<CheckUserResponse> isStudentExist(@Body CheckUserRequest checkUserRequest);

    @Multipart
    @POST("register")
    Call<ResponseBody> registerUser(
            @Part("StudentNumber") RequestBody stNum,
            @Part("StudentPassword") RequestBody stPass,
            @Part("Username") RequestBody username,
            @Part("Email") RequestBody email,
            @Part("Password") RequestBody password,
            @Part MultipartBody.Part profileImage
    );

    @POST("resend-confirmation-email")
    Call<ResponseBody> resendConfirm(@Query("email") String email);

    @POST("verify-email")
    Call<VerifyEmailResponse> verifyEmail(
            @Query("email") String email,
            @Query("token") String token
    );

    @POST("sync-account")
    Call<ResponseBody> syncAccount();

    @GET("personal-information")
    Call<PersonalInformation> personalInformation();

    @POST("refresh-token")
    Call<LoginResponse> refreshToken(@Query("token") String token);

    @GET("lessons-and-marks")
    Call<List<LessonAndMarks>> lessonsAndMarks();

    @GET("timetable")
    Call<List<Lesson>> lessons();

    @GET("meta/wake-up")
    Call<ResponseBody> wakeUp();

    @GET("posts")
    Call<List<Post>> posts();

    @Multipart
    @POST("posts")
    Call<ResponseBody> createPost(
            @Part("Body") RequestBody body,
            @Part List<MultipartBody.Part> pictures,
            @Part("Tags") List<String> tags
    );

    @DELETE("posts")
    Call<ResponseBody> deletePost(@Body DeletePostRequest deletePostRequest);

    @PATCH("posts/like")
    Call<ResponseBody> likePost(@Body LikeRequest likeRequest);

    @POST("comments")
    Call<CommentResponse> createComment(@Body CreateCommentRequest commentRequest);

    @GET("comments/{postId}")
    Call<List<Comment>> showComments(@Path("postId") int postId);
}
