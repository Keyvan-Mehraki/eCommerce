package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button JoinNowButton , LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JoinNowButton = (Button) findViewById(R.id.main_join_now_btn);
        LoginButton = (Button) findViewById(R.id.main_login_btn);

    }
}
