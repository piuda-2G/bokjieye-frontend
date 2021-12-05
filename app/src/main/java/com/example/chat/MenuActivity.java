package com.example.chat;

import static android.speech.tts.TextToSpeech.ERROR;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.Locale;

public class MenuActivity extends AppCompatActivity {
    Button chatbotButton;
    RelativeLayout recentInfo;
    Button benefitInfoButton;

    final int PERMISSION = 1;
    //tts
    private TextToSpeech tts;
    private SpeechRecognizer mRecognizer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //GET Permission
        if ( Build.VERSION.SDK_INT >= 19 ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO,Manifest.permission.CALL_PHONE},PERMISSION);

        }
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {

                        }

                        @Override
                        public void onError(String utteranceId) {
                        }

                        @Override
                        public void onStart(String utteranceId) {

                            System.out.println("tts start@");
                        }
                    });
                }

            }

        });
        String hello="안녕하세요 복지아이입니다";
        tts.speak(hello,TextToSpeech.QUEUE_FLUSH, null);

        chatbotButton  = (Button) findViewById(R.id.chat_button);

        recentInfo = (RelativeLayout) findViewById(R.id.newinfoBtn);
        benefitInfoButton = (Button) findViewById(R.id.benefitBtn);

        chatbotButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        recentInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), RecentinfoActivity.class);
                startActivity(intent);
            }
        });
        benefitInfoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}