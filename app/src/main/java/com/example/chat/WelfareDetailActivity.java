package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WelfareDetailActivity extends AppCompatActivity {

    TextView titleview;
    TextView contentview;
    TextView targetview;
    TextView departmentview;
    TextView phoneview;

    LinearLayout container;
    TextView newphone;
    TextView newname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welfare_detail);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        System.out.println("id received!"+id);
        String URL = "http://15.165.111.247/bokjiro/"+id;
        try {
            sendData(URL);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    public void get(String requestURL) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(requestURL)
                    .build(); //GET Request
            System.out.println("server request url"+requestURL);

            Response response = client.newCall(request).execute();
            System.out.println("request success");
            //출력
            String message = response.body().string();
            System.out.println("body"+message);
            parse_Json(message);

        } catch (Exception e){
            System.err.println("server connect Error"+e.toString());
        }
    }

    //리스트에 관련주제 목록 추가
    private void parse_Json(String list) throws JSONException {


//        JSONArray jsonarray = new JSONArray(list);
//
//        for(int i = 0; i < jsonarray.length(); i++) {
//            JSONObject jsonObj = jsonarray.getJSONObject(i);
//            String id = jsonObj.getString("id");
//            String title = jsonObj.getString("title");
//            String contents = jsonObj.getString("contents");
//            System.out.println("jsontest"+jsonObj);
//            System.out.println("json id"+id);
//            System.out.println("json title"+title);
//
//
        JSONObject jsonobj = new JSONObject(list);
        String title =  jsonobj.getString("title");
        String contents =  jsonobj.getString("contents");
        String target =  jsonobj.getString("target");
        String department =  jsonobj.getString("department");
        JSONArray phones = (JSONArray) jsonobj.getJSONArray("phones");

        for(int i=0;i<phones.length();i++){
            JSONObject item = (JSONObject)phones.get(i);
            System.out.println(item.getString("number"));
            String phone = item.getString("number");
            String name = item.getString("name");
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                titleview = findViewById(R.id.welfare_title);
                contentview = findViewById(R.id.welfare_content);
                targetview = findViewById(R.id.welfare_target);
                departmentview = findViewById(R.id.welfare_department);
                phoneview = findViewById(R.id.welfare_phone);

                container = (LinearLayout) findViewById(R.id.content_layout);

                titleview.setText(title);
                contentview.setText(contents);
                targetview.setText(target);
                departmentview.setText(department);

            }
        });


        System.out.println("id------"+target);
        System.out.println("id------"+phones);

        System.out.println("********");



    }


    }