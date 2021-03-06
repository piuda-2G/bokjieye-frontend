package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.state.State;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.adapters.ChatAdapter;
import com.example.chat.models.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.speech.tts.TextToSpeech.ERROR;

import static java.lang.Thread.sleep;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    RecyclerView chatView;
    ChatAdapter chatAdapter;
    List<Message> messageList = new ArrayList<>();
    EditText editMessage;
    ImageButton btnSend;

    Button button1;
    Button button2;
    Button sttBtn;
    TextView sttText;
    Switch keyboardswitch;
    ConstraintLayout keyboard;
    int keyboard_label = 1;
    //TextView textView1;
    Intent intent;
    final int PERMISSION = 1;


    String text;    //??????
    String reply;   //??????
    String result; //?????? ?????? ?????? ????????????
    String title; // ?????? ??????
    String classfication; //???????????? ??????
    String department; // ????????????
    String address; // ?????? ??????
    String reply_result;    //?????? ?????? ?????? ??????

    static String sessonId = UUID.randomUUID().toString();
    private HttpConnection httpConn = HttpConnection.getInstance();
    private TextToSpeech tts;
    private SpeechRecognizer mRecognizer;
    int final_label = 0;
    int voice_label = 0;

    //?????? ??????
    Boolean call_flag = false;
    Boolean phone_flag = false; // tts phone flag
    String request_id = null;
    Boolean result_flag = false;
    String call_message;

    String tel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatView = findViewById(R.id.chatView);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);
        chatAdapter = new ChatAdapter(messageList, this);
        chatView.setAdapter(chatAdapter);

        if ( Build.VERSION.SDK_INT >= 19 ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO,Manifest.permission.CALL_PHONE},PERMISSION);

        }


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // ????????? ????????????.
                    tts.setLanguage(Locale.KOREAN);
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {
                            if(result_flag == true){
                                System.out.println("this is result_flag");

                            }
                            if(voice_label == 1 && phone_flag == false && result_flag == false) {
                                System.out.println("speak start");
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
                                        mRecognizer.setRecognitionListener(listener);
                                        mRecognizer.startListening(intent);
                                    }
                                });
                            }

                            if(phone_flag == true) {
                                phone_flag = false;
                                System.out.println("call success");
                                try{

                                    startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
                                } catch (Exception e){
                                    System.out.println("phone"+e.toString());
                                }


                            }



                        }

                        @Override
                        public void onError(String utteranceId) {
                        }

                        @Override
                        public void onStart(String utteranceId) {
                            if(utteranceId.equals("call_logic")){
                                System.out.println("call_logic called"+utteranceId);
                                result_flag = false;
                            }
                            System.out.println("tts start@");

                        }
                    });
                }

            }


        });

        // STT ?????????----------------------------------------------------

        tts = new TextToSpeech(MainActivity.this, this);


        // ?????? ????????? ??????
        sttBtn = (Button)findViewById(R.id.sttbtn);
        sttText = (TextView)findViewById(R.id.stttext);
        keyboardswitch = (Switch) findViewById(R.id.keyboardSwitch);
        keyboardswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    editMessage.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.VISIBLE);
                    sttBtn.setVisibility(View.GONE);
                    sttText.setVisibility(View.GONE);
                } else {
                    // The toggle is disabled
                    editMessage.setVisibility(View.GONE);
                    btnSend.setVisibility(View.GONE);
                    sttBtn.setVisibility(View.VISIBLE);
                    sttText.setVisibility(View.VISIBLE);
                }
            }
        });
        //stt ?????? ????????? ???
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");   // ???????????? ???????????? ?????? ??????

        sttBtn.setOnClickListener(v -> {
            if (tts != null) {
                tts.stop();
            }

            if(mRecognizer!=null) {
                mRecognizer.destroy();
                mRecognizer.cancel();
                mRecognizer=null;
            }

            mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intent);
            voice_label = 1;
        });

        //?????? ????????????
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                voice_label = 0;
                String message = editMessage.getText().toString();
                text = message;
                if (!message.isEmpty()) {
                    messageList.add(new Message(message, false));
                    editMessage.setText("");
                    sendData();
                    Objects.requireNonNull(chatView.getAdapter()).notifyDataSetChanged();
                    Objects.requireNonNull(chatView.getLayoutManager())
                            .scrollToPosition(messageList.size() - 1);
                } else {
                    //Toast.makeText(MainActivity.this, "Please enter text!", Toast.LENGTH_SHORT).show();
                    System.out.println("btnsend else");
                }
            }
        });

    }
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"??????????????? ???????????????.",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "????????? ??????";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "??????????????? ??????";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "????????? ??????";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "???????????? ??????";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "????????? ????????????";
                    break;
//                case SpeechRecognizer.ERROR_NO_MATCH:
//                    message = "?????? ??? ??????";
//                    break;
//                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
//                    message = "RECOGNIZER??? ??????";
//                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "????????? ?????????";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "????????? ????????????";
                    break;
                default:
                    message ="";
                    break;
//                    message = "??? ??? ?????? ?????????";
//                    break;
            }

           // Toast.makeText(getApplicationContext(), "????????? ?????????????????????. : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // ?????? ?????? ArrayList??? ????????? ?????? textView??? ????????? ????????????.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            text = "";
            for(int i = 0; i < matches.size() ; i++){
                //textView1.setText(matches.get(i));
                text += matches.get(i);

            }

            System.out.println("send data:"+text);
            if (!text.isEmpty()) {
                messageList.add(new Message(text, false));
                editMessage.setText("");
                sendData();
                Objects.requireNonNull(chatView.getAdapter()).notifyDataSetChanged();
                Objects.requireNonNull(chatView.getLayoutManager())
                        .scrollToPosition(messageList.size() - 1);
                mRecognizer.destroy();
            } else {
               if(phone_flag != true) {
                   Toast.makeText(MainActivity.this, "Please enter text!", Toast.LENGTH_SHORT).show();

               }
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            ArrayList<String> data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            ArrayList<String> unstableData = partialResults.getStringArrayList("android.speech.extra.UNSTABLE_TEXT");
            System.out.println("test"+data.get(0) + unstableData.get(0));
        }

        @Override
        public void onEvent(int eventType, Bundle params) {}
    };



    /** ??? ????????? ????????? ?????? */
    private void sendData() {
// ???????????? ???????????? ????????? ????????? ?????????????????? ???????????? ??????
        new Thread() {
            public void run() {
// ???????????? 2?????? ?????????????????? ??????????????? ??????????????? ???????????? ??????
                httpConn.requestWebServer(text, callback, sessonId, request_id);
            }
        }.start();;
    }

    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("test", "????????????:"+e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            call_flag = false;
            String body = response.body().string();
            System.out.println("??????:"+body);
            try {
                JSONObject jsonObject = new JSONObject(body);
                reply = (String)jsonObject.getString("result texts");
                try {
                    if(jsonObject.has("sessionInit")) {
                        sessonId = UUID.randomUUID().toString();
                    }
                    if(jsonObject.has("resultData")){
                        request_id=null;
                        result = (String)jsonObject.getString("resultData");
                        JSONObject resultObject = new JSONObject(result);
                        // ????????? ?????????
                        if(jsonObject.has("fromBokjiro")){
                            title = (String)resultObject.getString("title");
                            classfication = (String)resultObject.getString("classification").trim();
                            request_id=resultObject.getString("id");
                            //??????????????? ??????
                            if(classfication.equals("????????????")) {
                                department = (String)resultObject.getString("department");
                                reply_result = department + "?????? ???????????? " + title + "????????? ???????????????.";

                            }
                            //???????????? ??????
                            else{
                                address = (String)resultObject.getString("address");
                                reply_result = address + "?????? ???????????? " + title + "????????? ???????????????.";
                            }
                        }
                        //????????? ????????? ?????? ?????? (Q&A)
                        else {
                            title = (String)resultObject.getString("title");
                            String contents = (String)resultObject.getString("contents");
                            reply_result = title +"?????? ????????? ????????????. ?????? ????????? ?????? ????????? "+contents;
                        }
                        if(jsonObject.has("resultData") && jsonObject.has("fromRecommend")){
                            call_flag = true;
                        }
                    }
                    //????????????
                    if(jsonObject.has("call")){
                        //???????????? ????????? call?????? true?????? ?????? ??????
                        if(jsonObject.getBoolean("call")){

                            tel = "tel:" + jsonObject.getString("number");
                            System.out.println("tel ---"+ tel);
                            System.out.println("bool ---"+ jsonObject.getBoolean("exist"));
                            if(jsonObject.getBoolean("exist")) {
                                System.out.println("local ---");
                                String not_welfare = "?????? ????????? ??????????????? ????????? ???????????????";
                                phone_flag =true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageList.add(new Message(not_welfare,true));
                                        chatAdapter.notifyDataSetChanged();
                                        Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);


                                    }
                                });

                                HashMap<String, String> params = new HashMap<String, String>();

                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"stringId");
                                tts.speak(not_welfare,TextToSpeech.QUEUE_FLUSH, params);

                            }
                            if(!jsonObject.getBoolean("exist")){
                                //?????????????????? ??????
                                System.out.println("local ---");
                                String not_welfare = "?????? ????????? ?????? ????????? ??????????????? ???????????? ????????????. ????????? ?????? ?????????????????? ????????? ???????????????.";
                                phone_flag =true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageList.add(new Message(not_welfare,true));
                                        chatAdapter.notifyDataSetChanged();
                                        Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);

                                    }
                                });
                                HashMap<String, String> params = new HashMap<String, String>();

                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"stringId");
                                tts.speak(not_welfare,TextToSpeech.QUEUE_FLUSH, params);
                            }



                        }
                    }
                } catch (Exception e){
                    System.out.println(e);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!reply.isEmpty()) {
                            System.out.println("----reply"+reply);
                            if(reply.equals("?????? ?????? ????????? ????????? ????????????.") == true){
                                result_flag = true;
                                System.out.println("success true");
                            }
                            messageList.add(new Message(reply, true));
                            chatAdapter.notifyDataSetChanged();
                            Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);
                            HashMap<String, String> params = new HashMap<String, String>();

                            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"stringId");
                            tts.speak(reply,TextToSpeech.QUEUE_FLUSH, params);

                            // ?????? ?????? ??????
                            if(result != null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageList.add(new Message(reply_result, true));
                                        chatAdapter.notifyDataSetChanged();
                                        Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);

                                    }
                                });


                                tts.speak(reply_result,TextToSpeech.QUEUE_ADD, params);

                                result = null;
                            }
                            if ( call_flag == true){
                                call_message = "????????? ?????? ?????? ????????? ?????? ????????? ?????? ????????? ????????????????";
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageList.add(new Message(call_message,true));
                                        chatAdapter.notifyDataSetChanged();
                                        Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);


                                    }
                                });



                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"call_logic");
                                tts.speak(call_message,TextToSpeech.QUEUE_ADD, params);
                            }



                        } else{
                           // Toast.makeText(MainActivity.this, "Please enter text!", Toast.LENGTH_SHORT).show();
                            System.out.println("?????? ??????");
                        }


                    }
                });


            } catch (Exception e){
                System.out.println("JSON parsing Error "+e);
            }

            Log.d("response test", "???????????? ????????? Body:"+body);

        }
    };

    public void speakJust(String text) {

        // tts??? ???????????????, ??????????????????.
        if(!tts.isSpeaking()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else{
            System.out.println("tts used");
        }

    }
    @Override
    public void onInit(int status){
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.KOREAN);
            tts.speak("?????? ?????? ?????? ???????????????. ?????? ????????? ?????? ??????????????? ?????? ?????? ?????? ????????? ???????????????. ?????? ????????? ???????????? ?????? ?????? ??????????????? ??????????????????.", TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if(mRecognizer!=null) {
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer=null;
        }
        super.onDestroy();
    }
}