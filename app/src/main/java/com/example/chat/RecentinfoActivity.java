package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.adapters.WelfareAdapter;
import com.example.chat.models.Message;
import com.example.chat.models.Welfare;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecentinfoActivity extends AppCompatActivity {
    private static final String TAG = "RecentinfoActivity";
    RecyclerView recyclerView;
    WelfareAdapter recyclerViewAdapter;

    private HttpConnection httpConn = HttpConnection.getInstance();

    boolean isLoading = false;
    String text;
    String sessionId;

    //Contained welfare Service list
    ArrayList<Welfare> list = new ArrayList<>();

    CheckBox centralChk;
    CheckBox localChk;
    EditText searchText;
    ImageButton searchBtn;
    TextView searchView;
    LinearLayout refreshBtn;
    ConstraintLayout searchLayout;
    TextView noResult;


    String queryText;
    Boolean central_bool = false;
    Boolean local_bool = false;
    Boolean search_bool = false;
    //http://~~~?page= 까지만 전역
    String serverURL="http://15.165.111.247/paged-list/?page=";
    // 페이지 정보 10개 리스트 받으면 +1
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recentinfo);

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
                    TextView countText = (TextView) findViewById(R.id.info_abstract_count);
                    String text = "신규 "+responseJson.getString("new")+"건, 전체 "+responseJson.getString("total")+"건";
                    countText.setText(text);
                } catch (Exception e){
                    System.out.println(e);
                }
            }
        }.start();
        // CheckBox logic
        centralChk = findViewById(R.id.check_Central);
        localChk = findViewById(R.id.check_Local);

        //중앙부처 클릭 시
        centralChk.setOnClickListener(v -> {
            if(localChk.isChecked()){
                localChk.setChecked(false);
                local_bool = false;
            }

            list.clear();
            page = 1;
            central_bool = true;
            //String serverURL = "http://15.165.111.247/paged-list/?page="+page+"&central=1";
            if(!centralChk.isChecked()){
                System.out.println("체크 해제됨");
                central_bool = false;
            }
            try {
                sendData(serverURL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            initAdapter();
            initScrollListener();

        });
        //지자체 클릭 시
        localChk.setOnClickListener(v -> {
            if(centralChk.isChecked()){
                centralChk.setChecked(false);
                central_bool = false;
            }
            list.clear();
            page = 1;
            local_bool = true;
            if(!localChk.isChecked()){
                local_bool = false;
            }
            try {
                sendData(serverURL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            initAdapter();
            initScrollListener();

        });
        // search logic
        searchText = findViewById(R.id.search_Query);
        searchBtn = (ImageButton) findViewById(R.id.searchQuery_button);
        searchView = (TextView) findViewById(R.id.searchText);
        refreshBtn = (LinearLayout) findViewById(R.id.refresh_filter);
        searchLayout = (ConstraintLayout)findViewById(R.id.search_layout);

        //No search result
        noResult = (TextView) findViewById(R.id.noSearchResult);

        searchBtn.setOnClickListener(v -> {
            String message = searchText.getText().toString();
            searchText.setText("");
            list.clear();
            page = 1;
            search_bool = true;
            queryText = message;
            String returntext = "'"+message+"'에 관한 검색 결과입니다.";
            searchView.setText(returntext);
            searchLayout.setVisibility(View.VISIBLE);

            try {
                sendData(serverURL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            initAdapter();
            initScrollListener();

        });
        //필터 초기화 버튼
        refreshBtn.setOnClickListener(v -> {
            queryText = "";
            page = 1;
            search_bool = false;
            local_bool = false;
            central_bool = false;
            searchLayout.setVisibility(View.GONE);
            centralChk.setChecked(false);
            localChk.setChecked(false);
            list.clear();
            try {
                sendData(serverURL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            initAdapter();
            initScrollListener();

        });


        //RecyclerView for welfare list
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            sendData(serverURL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initAdapter();
        initScrollListener();
    }

    private void sendData(String requestURL) throws InterruptedException {
// 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출
        Thread thread = new Thread() {
            public void run() {
// 파라미터 2개와 미리정의해논 콜백함수를 매개변수로 전달하여 호출
                //httpConn.requestWelfareList(page, callback, sessionId);
//                String URL = (serverURL + page) requestURL;
//                if centralChecked => URL + "&local=1"
//                if key
                String URL = requestURL + page;
                if(central_bool == true) {
                    URL = URL + "&central=1";
                }
                if(local_bool == true) {
                    URL = URL + "&local=1";
                }
                if(search_bool == true) {
                    URL = URL + "&keyword="+queryText;
                }

                System.out.println("URL requeast !!---"+URL);

                get(URL);
            }
        };
        thread.start();
        thread.join();
    }




    private void dataMore() {
        System.out.println("page number "+page);
        try {
            sendData(serverURL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Add null because of Loading Progress Bar
        list.add(null);
        System.out.println("null add list "+list.size());
        recyclerViewAdapter.notifyItemInserted(list.size() -1 );
        System.out.println("null add list2 "+list.size());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                list.remove(list.size() -1 );
                System.out.println("null add list3 "+list);
                int scrollPosition = list.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);
                System.out.println("null add list4 "+list.size());
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                recyclerViewAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);

    }

    public void get(String requestURL) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(requestURL)
                    .build(); //GET Request

            //동기 처리시 execute함수 사용
            Response response = client.newCall(request).execute();

            //출력
            String body = response.body().string();
            System.out.println("server body test"+ body);
            try{
                JSONArray allresult = new JSONArray(body);
                if(allresult.length() == 0 ){
                    noResult.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < allresult.length(); i++) {
                    JSONObject item = allresult.getJSONObject(i).getJSONObject("_source");
                    System.out.println("asdf" + item);
                    String id = item.getString("id");
                    String title = item.getString("title");
                    String content = item.getString("contents");

                    list.add(new Welfare(id, title, content));
                    System.out.println("list size"+list.size());
                }
                System.out.println("finish!!"+page);

                page = page + 1;



            } catch (Exception e) {
                System.err.println(e.toString());
            }
        } catch (Exception e){
            System.err.println(e.toString());
        }
    }







    private void initAdapter() {
        System.out.println("init list "+list.size());
        recyclerViewAdapter = new WelfareAdapter(this,list);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    // 리싸이클러뷰 이벤트시
    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged: ");
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: ");

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == list.size() -1 ) {

                        dataMore();
                        isLoading = true;
                        //Toast.makeText(RecentinfoActivity.this, "스크롤", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

}