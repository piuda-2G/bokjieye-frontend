package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WelfareDetailActivity extends AppCompatActivity {

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
        JSONObject jsonobj = new JSONObject(list).getJSONObject("_source");
        try{

        } catch (Exception e){
            System.out.println("address parse error"+e.toString());
        }
        String address = jsonobj.getString("address");
        String age = jsonobj.getString("age");
        String classification = jsonobj.getString("classification");
        String contents = jsonobj.getString("contents");
        String department = jsonobj.getString("department");
        String interest = jsonobj.getString("interest");
        String lifecycle = jsonobj.getString("ifecycle");
        String phone = jsonobj.getString("phone");
        String title = jsonobj.getString("title");
        System.out.println("----");
        System.out.println("age"+age);
        System.out.println("classification"+classification);
        System.out.println("contents"+contents);
    }


    }