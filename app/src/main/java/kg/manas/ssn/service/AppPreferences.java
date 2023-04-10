package kg.manas.ssn.service;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import kg.manas.ssn.service.model.Lesson;
import kg.manas.ssn.service.model.LessonAndMarks;
import kg.manas.ssn.service.model.PersonalInformation;
import kg.manas.ssn.service.model.Post;
import kg.manas.ssn.utils.BusinessUtils;

public class AppPreferences extends Application {

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSS'Z'";
    public static final String NAME = "SSN_preferences";
    public static final String TIME_TABLE = "time_table";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String IS_LOGGED_IN = "isLogin";
    public static final String STARTED = "started";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String STUDENT_NUMBER = "studentNumber";
    public static final String PASSWORD = "password";
    public static final String POSTS = "posts";
    public static final String PERSONAL_INFORMATION = "personal_information";
    public static final String LESSONS_AND_MARKS = "lessons_and_marks";
    public static final String REFRESH_TOKEN_EXPIRED = "refresh_token_expired";
    public static final String ACCESS_TOKEN_EXPIRED = "access_token_expired";

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        context = getApplicationContext();
    }

    public static String getAccessToken() {
        return sharedPreferences.getString(ACCESS_TOKEN, "");
    }

    public static void setAccessToken(String access_token) {
        editor.putString(ACCESS_TOKEN, access_token);
        editor.apply();
    }

    public static DateTime getAccessTokenExpired() {
        return BusinessUtils.stringToDateTime(FORMAT,sharedPreferences.getString(REFRESH_TOKEN_EXPIRED, "1999-7-30T13:39:45.618Z"));
    }

    public static void setAccessTokenExpired(String access_token) {
        editor.putString(ACCESS_TOKEN_EXPIRED, access_token);
        editor.apply();
    }

    public static String getRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN, "");
    }

    public static void setRefreshToken(String refresh_token) {
        editor.putString(REFRESH_TOKEN, refresh_token);
        editor.apply();
    }

    public static DateTime getRefreshTokenExpired() {
        return BusinessUtils.stringToDateTime(FORMAT,sharedPreferences.getString(ACCESS_TOKEN_EXPIRED, "1999-7-30T13:39:45.618Z"));
    }

    public static void setRefreshTokenExpired(String refresh_token) {
        editor.putString(REFRESH_TOKEN_EXPIRED, refresh_token);
        editor.apply();
    }

    public static String getUserName() {
        return sharedPreferences.getString(USERNAME, null);
    }

    public static void setUserName(String userName) {
        editor.putString(USERNAME, userName);
        editor.apply();
    }

    public static String getStudentNumber() {
        return sharedPreferences.getString(STUDENT_NUMBER, null);
    }

    public static void setStudentNumber(String studentNumber) {
        editor.putString(STUDENT_NUMBER, studentNumber);
        editor.apply();
    }

    public static String getPassword() {
        return sharedPreferences.getString(PASSWORD, null);
    }

    public static void setPassword(String password) {
        editor.putString(PASSWORD, password);
        editor.apply();
    }

    public static boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public static void setIsLoggedIn(boolean login) {
        editor.putBoolean(IS_LOGGED_IN, login);
        editor.apply();
    }

    public static boolean isStarted() {
        return sharedPreferences.getBoolean(STARTED, false);
    }

    public static void setStarted(boolean started) {
        editor.putBoolean(STARTED, started);
    }

    public static void setEmail(String email) {
        editor.putString(EMAIL, email);
    }

    public static String getEmail() {
        return sharedPreferences.getString(EMAIL, "");
    }

    public static ArrayList<Post> loadPosts() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(POSTS, "[]");
        Type type = new TypeToken<ArrayList<Post>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void savePosts(List<Post> postList) {
        Gson gson = new Gson();
        String json = gson.toJson(postList);
        editor.putString(POSTS, json);
        editor.apply();
    }

    public static void saveLessonsAndMarks(List<LessonAndMarks> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(LESSONS_AND_MARKS, json);
        editor.apply();
    }

    public static ArrayList<LessonAndMarks> loadLessonsAndMarks() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(LESSONS_AND_MARKS, "[]");
        Type type = new TypeToken<ArrayList<LessonAndMarks>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void saveLessons(List<Lesson> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(TIME_TABLE, json);
        editor.apply();
    }

    public static ArrayList<Lesson> loadLessons() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(TIME_TABLE, "[]");
        Type type = new TypeToken<ArrayList<Lesson>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void savePersonalInformation(PersonalInformation personalInformation) {
        Gson gson = new Gson();
        String json = gson.toJson(personalInformation);
        editor.putString(PERSONAL_INFORMATION, json);
        editor.apply();
    }

    public static PersonalInformation loadPersonalInformation() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PERSONAL_INFORMATION, null);
        Type type = new TypeToken<PersonalInformation>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void clearAll() {
        editor.clear().apply();
    }
    public Application getInstance(){
        return this;
    }
}
