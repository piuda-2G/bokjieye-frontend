package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){

//                String main_text = "aaaaaa";
//
//                int main_number = 20;

                Intent startIntent = new Intent(SplashActivity.this, MenuActivity.class);

//                startIntent.putExtra("info",main_text);
//                startIntent.putExtra("info_num",main_number);

                startActivity(startIntent);

                finish();
            }
        }, 2000);


    }
}