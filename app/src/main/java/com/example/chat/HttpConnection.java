package com.example.chat;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpConnection {
    private String ServerURL;
    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static HttpConnection getInstance() {
        return instance;
    }

    private HttpConnection(){ this.client = new OkHttpClient(); }


    /** 웹 서버로 요청을 한다. */
    public void requestWebServer(String parameter, Callback callback, String sesson, String request_id) {
        try {
            JSONObject json = new JSONObject();
            json.put("texts", parameter);
            json.put("sessionId", sesson);
            if(request_id != null){
                json.put("request_id",request_id);
            }
            RequestBody body = RequestBody.create(JSON,json.toString());

            Request request = new Request.Builder()
                    .url("http://15.165.111.247/chatting/")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(callback);
        }catch (Exception e) {
            System.out.println("requestWebServer error");
        }
    }
    public void requestWelfareList(int parameter, Callback callback, String sesson) {
        try {
            ServerURL = "http://15.165.111.247/paged-list/?page="+parameter+"&central=1&local=1";
//            JSONObject json = new JSONObject();
//            json.put("texts", parameter);
//            json.put("sessionId", sesson);
//            RequestBody body = RequestBody.create(JSON,json.toString());

            Request request = new Request.Builder()
                    .url(ServerURL)
                    .build();
            client.newCall(request).enqueue(callback);
        }catch (Exception e) {
            System.out.println("request error"+e.toString());
        }
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
            String message = response.body().string();
            System.out.println("test server "+ message);
        } catch (Exception e){
            System.err.println(e.toString());
        }
    }



}