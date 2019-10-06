package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import android.speech.tts.TextToSpeech;


public class VoiceActivity extends AppCompatActivity {

    Intent intent;
    SpeechRecognizer mRecognizer;
    ImageView imageView;
    TextView textView;
    TextToSpeech tts;
    ImageButton imageButton;


    boolean check = false;
    int ran = 4;
    int n = 4;
    int sizeLevel = 100;
    int x = 500;
    int y = 500;
    int acnt = 0;
    int icnt = 1;
    double result = 0.1;
    final int PERMISSION = 1;

    Bundle results;
    ArrayList<String> matches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_main);

        if ( Build.VERSION.SDK_INT >= 21 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }


        imageView = (ImageView)findViewById(R.id.eye_image);

        changeImage();


        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(intent);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.KOREAN);
            }
        });

    }

    public void reStart(){
        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {

            @Override
            public void run() {
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(VoiceActivity.this);
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(intent);
                acnt++;
            }
        }, 1000);
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
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            //ArrayList<String> matches =
            //        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            replyAnswer(matches.get(0));
            if(acnt >= 3){
                testEnd();
            }
            else if(icnt <= 9){
                reStart();
            }
            else
                testEnd();
            if(check == true){
                iLevel();
            }

        }

        private void replyAnswer(String matches){
            if((ran == 1 && matches.equals("위쪽"))||(ran == 2 && matches.equals("아래쪽"))||(ran == 3 && matches.equals("왼쪽"))||(ran == 4 && matches.equals("오른쪽"))){
                tts.speak("정답입니다"  , TextToSpeech.QUEUE_FLUSH,null);
                check = true;
            }
            else if((ran == 1 && matches.equals("아래쪽") || matches.equals("왼쪽") || matches.equals("오른쪽")) || (ran == 2 && matches.equals("위쪽") || matches.equals("왼쪽") || matches.equals("오른쪽")) || (ran == 3 && matches.equals("아래쪽") || matches.equals("위쪽") || matches.equals("오른쪽")) || (ran == 4 && matches.equals("아래쪽") || matches.equals("왼쪽") || matches.equals("위쪽"))){
                tts.speak("오답입니다" , TextToSpeech.QUEUE_FLUSH, null);
                check = false;
            }
            else if(matches.equals("종료")){
                finish();
            }
            else {
                tts.speak("다시 말해주세요", TextToSpeech.QUEUE_FLUSH,null);
                if(acnt > 0){
                    acnt--;
                }
                check = false;
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    };

    public  void iLevel(){
        switch (icnt) {
            case 1:
                acnt = 0;
                changeLevel();
                icnt++;
                break;
            case 2:
                //sizeLevel += 2;
                acnt = 0;
                changeLevel();
                icnt++;
                break;
            case 3:
                acnt = 0;
                changeLevel();
                icnt++;
                break;

            case 4:
                //sizeLevel += 2;
                acnt = 0;
                result = 0.3;
                changeLevel();
                icnt++;
                break;
            case 5:
                acnt = 0;
                changeLevel();
                icnt++;
                break;
            case 6:
                acnt = 0;
                result = 0.5;
                changeLevel();
                icnt++;
                break;
            case 7:
                acnt = 0;
                changeLevel();
                icnt++;
                break;
            case 8:
                acnt = 0;
                result = 0.7;
                changeLevel();
                icnt++;
                break;
            case 9:
                acnt = 0;
                changeLevel();
                icnt++;
                break;
            case 10:
                result = 1.0;
                break;
        }

    }
    public void testEnd(){
        setContentView(R.layout.voice_end);
        textView = (TextView)findViewById(R.id.eye_text);
        imageButton = (ImageButton)findViewById(R.id.exit_button);
        tts.speak("검사가 종료되었습니다 당신의 시력은 " + result + " 입니다", TextToSpeech.QUEUE_FLUSH,null);
        textView.setText("당신은 시력은 "+ result + " 입니다");
        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        mRecognizer.destroy();
    }
    @Override
    protected  void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    public void intRan() {
        int Random = ran;
        ran = (int) (Math.random() * 4) + 1;
        while (Random == ran)
            ran = (int) (Math.random() * 4) + 1;

    }

    public void changeLevel(){
        switch(icnt){
            case 1:
                changeImage();
                break;
            case 2:
                imageView.getLayoutParams().height = x-sizeLevel*3;
                imageView.getLayoutParams().width = y-sizeLevel*3;
                imageView.requestLayout();
                changeImage();
                break;
            case 3:
                changeImage();
                break;
            case 4:
                imageView.getLayoutParams().height = (int) (x-sizeLevel*3.5);
                imageView.getLayoutParams().width = (int) (y-sizeLevel*3.5);
                imageView.requestLayout();
                changeImage();
                break;
            case 5:
                changeImage();
                break;
            case 6:
                imageView.getLayoutParams().height = x-sizeLevel*4;
                imageView.getLayoutParams().width = y-sizeLevel*4;
                imageView.requestLayout();
                changeImage();
                break;
            case 7:
                changeImage();
                break;
            case 8:
                imageView.getLayoutParams().height = (int) (x-sizeLevel*4.5);
                imageView.getLayoutParams().width = (int) (y-sizeLevel*4.5);
                imageView.requestLayout();
                changeImage();
                break;
            case 9:
                changeImage();
                break;
        }
    }

    public void changeImage(){
        intRan();
        switch(ran){
            case 1:
                imageView.setImageResource(R.drawable.uc);
                break;
            case 2:
                imageView.setImageResource(R.drawable.dc);
                break;
            case 3:
                imageView.setImageResource(R.drawable.lc);
                break;
            case 4:
                imageView.setImageResource(R.drawable.c);
                break;
        }
    }

}