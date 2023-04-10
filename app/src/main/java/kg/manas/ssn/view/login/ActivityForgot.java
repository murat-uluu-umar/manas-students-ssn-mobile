package kg.manas.ssn.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kg.manas.ssn.R;
import kg.manas.ssn.view.ActivityMain;

public class ActivityForgot extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        ImageButton backButton = findViewById(R.id.back_bt);
        backButton.setOnClickListener((l)->{
            startActivity(new Intent(this, ActivityLogin.class));
            finish();
        });
    }
}