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


    String text;    //질문
    String reply;   //응답
    String result; //최종 결과 전부 들어있음
    String title; // 복지 제목
    String classfication; //중앙부처 여부
    String department; // 담당부처
    String address; // 관할 지역
    String reply_result;    //최종 복지 결과 대답

    static String sessonId = UUID.randomUUID().toString();
    private HttpConnection httpConn = HttpConnection.getInstance();
    private TextToSpeech tts;
    private SpeechRecognizer mRecognizer;
    int final_label = 0;
    int voice_label = 0;

    //전화 연결
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
                    // 언어를 선택한다.
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

        // STT 테스트----------------------------------------------------

        tts = new TextToSpeech(MainActivity.this, this);


        // 자판 보이게 하기
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
        //stt 버튼 눌렀을 때
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");   // 텍스트로 변환시킬 언어 설정

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

        //채팅 보냈을때
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
            Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.",Toast.LENGTH_SHORT).show();
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
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
//                case SpeechRecognizer.ERROR_NO_MATCH:
//                    message = "찾을 수 없음";
//                    break;
//                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
//                    message = "RECOGNIZER가 바쁨";
//                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message ="";
                    break;
//                    message = "알 수 없는 오류임";
//                    break;
            }

           // Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어준다.
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



    /** 웹 서버로 데이터 전송 */
    private void sendData() {
// 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출
        new Thread() {
            public void run() {
// 파라미터 2개와 미리정의해논 콜백함수를 매개변수로 전달하여 호출
                httpConn.requestWebServer(text, callback, sessonId, request_id);
            }
        }.start();;
    }

    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("test", "콜백오류:"+e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            call_flag = false;
            String body = response.body().string();
            System.out.println("바디:"+body);
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
                        // 복지로 데이터
                        if(jsonObject.has("fromBokjiro")){
                            title = (String)resultObject.getString("title");
                            classfication = (String)resultObject.getString("classification").trim();
                            request_id=resultObject.getString("id");
                            //중앙부처일 경우
                            if(classfication.equals("중앙부처")) {
                                department = (String)resultObject.getString("department");
                                reply_result = department + "에서 주관하는 " + title + "사업이 존재합니다.";

                            }
                            //지자체일 경우
                            else{
                                address = (String)resultObject.getString("address");
                                reply_result = address + "에서 주관하는 " + title + "사업이 존재합니다.";
                            }
                        }
                        //복지로 데이터 아닌 경우 (Q&A)
                        else {
                            title = (String)resultObject.getString("title");
                            String contents = (String)resultObject.getString("contents");
                            reply_result = title +"라는 질문이 있습니다. 해당 질문에 대한 답변은 "+contents;
                        }
                        if(jsonObject.has("resultData") && jsonObject.has("fromRecommend")){
                            call_flag = true;
                        }
                    }
                    //전화연결
                    if(jsonObject.has("call")){
                        //서버에서 넘어온 call값이 true이면 전화 연결
                        if(jsonObject.getBoolean("call")){

                            tel = "tel:" + jsonObject.getString("number");
                            System.out.println("tel ---"+ tel);
                            System.out.println("bool ---"+ jsonObject.getBoolean("exist"));
                            if(jsonObject.getBoolean("exist")) {
                                System.out.println("local ---");
                                String not_welfare = "해당 복지의 담당부처로 전화를 연결합니다";
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
                                //보건복지부로 연결
                                System.out.println("local ---");
                                String not_welfare = "해당 복지의 연락 가능한 전화번호가 존재하지 않습니다. 상담을 위해 보건복지부로 전화를 연결합니다.";
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
                            if(reply.equals("추천 복지 정보는 다음과 같습니다.") == true){
                                result_flag = true;
                                System.out.println("success true");
                            }
                            messageList.add(new Message(reply, true));
                            chatAdapter.notifyDataSetChanged();
                            Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);
                            HashMap<String, String> params = new HashMap<String, String>();

                            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"stringId");
                            tts.speak(reply,TextToSpeech.QUEUE_FLUSH, params);

                            // 최종 결과 반환
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
                                call_message = "상담을 위해 복지 서비스 담당 부처에 전화 연결을 진행할까요?";
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
                            System.out.println("응답 에러");
                        }


                    }
                });


            } catch (Exception e){
                System.out.println("JSON parsing Error "+e);
            }

            Log.d("response test", "서버에서 응답한 Body:"+body);

        }
    };

    public void speakJust(String text) {

        // tts가 사용중이면, 말하지않는다.
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
            tts.speak("복지 정보 채팅 기능입니다. 해당 기능은 복지 추천기능과 복지 질문 검색 기능이 존재합니다. 하단 버튼을 클릭하여 추천 또는 검색이라고 말씀해주세요.", TextToSpeech.QUEUE_FLUSH, null);
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