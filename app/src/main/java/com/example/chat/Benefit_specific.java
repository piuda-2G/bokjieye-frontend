package com.example.chat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.adapters.Benefit_Info_ListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Benefit_specific extends AppCompatActivity {

    private TextView textview;
    private ListView listView;
    private Benefit_Info_ListAdapter adapter;
    private List<Benefit_Info> b_List;
    public String front_info;
    public String sub_text;
    public String sub_text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefit_specific);

        Intent intent  = getIntent();
        sub_text = intent.getStringExtra("info"); //이전액티비티에서 받아온 정보(title)
        sub_text2 = intent.getStringExtra("info2");
        front_info = sub_text2;

        // 서버연결
        //String u = "http://15.165.111.247/benefit/카테고리이름/";
        String u = "http://15.165.111.247/benefit/";
        try {
            u = u + URLEncoder.encode(front_info, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("url"+e.toString());
        }


        try {

            System.out.println("test2");
            sendData(u);
        } catch (InterruptedException e) {
            System.out.println("sendData error"+e.toString());
        }

        System.out.println("test3");
        //선택한 카테고리로 이름 변경
        textview = findViewById(R.id.subject_n);
        textview.setText(sub_text);

        //listview와 list초기화
        listView = (ListView) findViewById(R.id.listView_B1);
        b_List = new ArrayList<Benefit_Info>();
        adapter = new Benefit_Info_ListAdapter(getApplicationContext(), b_List, sub_text);
        listView.setAdapter(adapter);



        //listview클릭시
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Log.i("click",String.valueOf(position));
                // Benefit_Info vo = (Benefit_Info)parent.getAdapter().getItem(position);
            }

        });
    }



    public void get(String requestURL) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(requestURL)
                    .build(); //GET Request
            System.out.println("server request url"+requestURL);
            //동기 처리시 execute함수 사용
            Response response = client.newCall(request).execute();
            /* System.out.println("request success");*/
            //출력
            String message = response.body().string();
         /*   System.out.println("body"+message);
            Log.i("aaaa",message);
*/
            add_list(message);

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

    //리스트에 관련주제 목록 추가  //정보 id 제목 내용 정보 얻어와 리스트에 추가
    private void add_list(String list) throws JSONException {


        JSONArray jsonarray = new JSONArray(list);

        for(int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonObj = jsonarray.getJSONObject(i);
            String id = jsonObj.getString("id");
            String title = jsonObj.getString("title");
            String contents = jsonObj.getString("contents");
          /*  System.out.println("jsontest"+jsonObj);
            System.out.println("json id"+id);
            System.out.println("json title"+title);
            System.out.println("json contents"+contents);*/

            Benefit_Info b_info = new Benefit_Info(id,title,contents);
           /* System.out.println("b_info"+b_info.getB_name());
            System.out.println("b_info"+b_info.getB_contents());*/

            b_List.add(b_info);

         /*   System.out.println("b_info"+b_info.getB_name());
            System.out.println("b_info"+b_info.getB_contents());
*/
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();

            }
        });

    }
}