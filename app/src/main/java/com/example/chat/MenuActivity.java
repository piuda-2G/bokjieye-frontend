package com.example.chat;

import static android.speech.tts.TextToSpeech.ERROR;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class MenuActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    Button chatbotButton;
    RelativeLayout recentInfo;
    Button benefitInfoButton;

    final int PERMISSION = 1;
    //tts
    private TextToSpeech tts;
    private SpeechRecognizer mRecognizer;

    String new_count;
    String total_count;
    static final int PERMISSIONS_REQUEST = 0x0000001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //GET Permission
        if ( Build.VERSION.SDK_INT >= 19 ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO,Manifest.permission.CALL_PHONE},PERMISSION);

        }




        new Thread() {
            public void run(){
                try{
                    URL url = new URL("http://15.165.111.247/count-items/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    JSONObject commands = new JSONObject();

                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    JSONObject responseJson = new JSONObject(sb.toString());

                    new_count = responseJson.getString("new");
                    total_count = responseJson.getString("total");

                } catch (Exception e){
                    System.out.println(e);
                }
            }
        }.start();



        chatbotButton  = (Button) findViewById(R.id.chat_button);

        recentInfo = (RelativeLayout) findViewById(R.id.newinfoBtn);
        benefitInfoButton = (Button) findViewById(R.id.benefitBtn);

        chatbotButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                if (tts != null) {
                    tts.stop();
                    tts.shutdown();
                }

            }
        });
        recentInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), RecentinfoActivity.class);
                startActivity(intent);
                if (tts != null) {
                    tts.stop();
                    tts.shutdown();
                }

            }
        });
        benefitInfoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), Benefit.class);
                startActivity(intent);
                if (tts != null) {
                    tts.stop();
                    tts.shutdown();
                }

            }
        });
    }
    @Override
    public void onInit(int status){
        System.out.println("처음 status"+status);
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.KOREAN);
            tts.speak("안녕하세요 복지 서비스 제공 어플 복지아이입니다.앱의 기능은 첫번째 복지관련 채팅상담 기능 두번째 최신복지 확인 기능 세번째 장애인 혜택 확인 기능이 존재합니다.",TextToSpeech.QUEUE_FLUSH, null);;

        }

    }
    public void speakJust(String text) {
        System.out.println("tts speakjust");

        // tts가 사용중이면, 말하지않는다.
        if(!tts.isSpeaking()) {

            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode) {

            case PERMISSIONS_REQUEST:

                if (grantResults.length > 0

                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tts = new TextToSpeech(MenuActivity.this, this);

                    // 권한 허가된 경우 처리

                } else {

                    Toast.makeText(this, "앱 실행을 위해서는 권한이 필요합니다.", Toast.LENGTH_LONG).show();

                }

                break;

        }

    }
    @Override
    protected void onResume() {
        super.onResume();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView newCount = (TextView) findViewById(R.id.center1);
                TextView totalCount = (TextView) findViewById(R.id.center3);

                newCount.setText(new_count);
                totalCount.setText(total_count);

            }
        });

    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }


}