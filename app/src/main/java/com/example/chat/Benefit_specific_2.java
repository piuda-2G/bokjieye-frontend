package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Benefit_specific_2 extends AppCompatActivity {

    Button back_btn;
    TextView subject_s;
    TextView B_name_s;
    TextView B_contents_s;
    TextView B_who_s;
    TextView B_howto_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefit_specific2);

        Intent intent = getIntent();


        subject_s = (TextView)findViewById(R.id.subject_n);
        B_name_s = (TextView)findViewById(R.id.b_name_2);
        B_contents_s = (TextView)findViewById(R.id.b_contents_2);
        B_who_s = (TextView)findViewById(R.id.b_who_2);  //아직 정보 X
        B_howto_s = (TextView)findViewById(R.id.b_howto_2); //아직 정보 X

        back_btn = (Button)findViewById(R.id.benefitinfo_back);

        String frontinfo = intent.getStringExtra("front_info2");
        String id = intent.getStringExtra("id");
/*      String name = intent.getStringExtra("name");
        String contents = intent.getStringExtra("contents");
        String whos = intent.getStringExtra("whos");  //정보없음
        String howto = intent.getStringExtra("howto"); //정보없음*/

        //상단 카테고리 제목 변경
        subject_s.setText(frontinfo);

        String u = "http://15.165.111.247/benefit/detail/";
        try {
            u = u + URLEncoder.encode(id, "UTF-8");
            System.out.println("url"+u);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        try {
            // System.out.println("test5");
            sendData(u);
        } catch (InterruptedException e) {
            System.out.println("sendData error"+e.toString());
        }


        //뒤로가기 버튼
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void get(String requestURL) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(requestURL)
                    .build(); //GET Request
            // System.out.println("server request url"+requestURL);
            //동기 처리시 execute함수 사용
            Response response = client.newCall(request).execute();
            // System.out.println("request success");
            //출력
            String message = response.body().string();
            // System.out.println("body"+message);
            // Log.i("aaaa",message);
            add_info(message);

        } catch (Exception e){
            System.err.println("server connect Error"+e.toString());
        }
    }

    private void sendData(String requestURL) throws InterruptedException {

// 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출
        Thread thread = new Thread() {
            public void run() {
                get(requestURL);
            }
        };
        thread.start();
    }


    //정보추가
    private void add_info(String list) throws JSONException {

        JSONObject jsonObj = new JSONObject(list);
        /*String id = jsonObj.getString("id");*/
        String title = jsonObj.getString("title");
        String contents = jsonObj.getString("contents");
        String who = jsonObj.getString("who");
        String howto = jsonObj.getString("howTo");

/*
            System.out.println("jsontest_1 "+jsonObj);
         //   System.out.println("json id_1 "+id);
            System.out.println("json title_1 "+title);
            System.out.println("json contents_1 "+contents);

*/

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //text변경
                B_name_s.setText(title);
                B_contents_s.setText(contents);
                B_who_s.setText(who);
                B_howto_s.setText(howto);
            }
        });
    }

}