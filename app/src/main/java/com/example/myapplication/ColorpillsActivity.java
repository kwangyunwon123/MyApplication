package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ColorpillsActivity extends AppCompatActivity {
    Button ansBtn1;
    Button ansBtn2;
    Button ansBtn3;
    TextView textView;
    ImageButton exitBtn;
    ImageView imageView;

    int iLevel = 1;
    int norScore = 0;
    int colPiScore = 0;
    int colBlScore = 0;
    int ran;
    String s;
    String r;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colorpills_main);

        ansBtn1 = (Button) findViewById(R.id.btn1);
        ansBtn2 = (Button) findViewById(R.id.btn2);
        ansBtn3 = (Button) findViewById(R.id.btn3);
        imageView = (ImageView)findViewById(R.id.image_colorpills);

        //textView.setVisibility(View.GONE);

        ansBtn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Btn1AnsCheck();
                iLevel++;
                if(iLevel == 7){
                    testEnd();
                }

                changeAll();
            }
        });
        ansBtn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Btn2AnsCheck();
                iLevel++;
                if(iLevel == 7){
                    testEnd();
                }

                changeAll();
            }
        });
        ansBtn3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Btn3AnsCheck();
                iLevel++;
                if(iLevel == 7){
                    testEnd();
                }

                changeAll();
            }
        });
    }

    public void changeAll(){
        changeImage();
        changeBtnText();
    }

    public void changeImage(){
        switch(iLevel){
            case 2:
                imageView.setImageResource(R.drawable.ti3_75);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ti4_8);
                break;
            case 4:
                imageView.setImageResource(R.drawable.ti5_48);
                break;
            case 5:
                imageView.setImageResource(R.drawable.ti8_7);
                break;
            case 6:
                imageView.setImageResource(R.drawable.ti10_66);
                break;
        }
    }

    public void changeBtnText(){
        intRan();
        s = Integer.toString(ran);
        //s = ""+ran;
        switch(iLevel){
            case 2:
                ansBtn1.setText(s);
                ansBtn2.setText("75");
                ansBtn3.setText("16");
                break;
            case 3:
                ansBtn1.setText("5");
                ansBtn2.setText(s);
                ansBtn3.setText("8");
                break;
            case 4:
                ansBtn1.setText("48");
                ansBtn2.setText(s);
                ansBtn3.setText("13");
                break;
            case 5:
                ansBtn1.setText("7");
                ansBtn2.setText("2");
                ansBtn3.setText(s);
                break;
            case 6:
                ansBtn1.setText(s);
                ansBtn2.setText("75");
                ansBtn3.setText("66");
                break;
        }

    }

    public void intRan(){
        switch(iLevel) {
            case 2:
                ran = (int) (Math.random() * 89) + 10;
                while(ran == 16 || ran == 75) {
                    ran = (int) (Math.random() * 89) + 10;
                }
                break;
            case 3:
                ran = (int) (Math.random()*8)+1;
                while(ran == 5 || ran == 8) {
                    ran = (int) (Math.random() * 8) + 1;
                }
                break;
            case 4:
                ran = (int) (Math.random()*89)+10;
                while(ran == 13 || ran == 48) {
                    ran = (int) (Math.random() * 89) + 10;
                }
                break;
            case 5:
                ran = (int) (Math.random()*8)+1;
                while(ran == 2 || ran == 7) {
                    ran = (int) (Math.random() * 8) + 1;
                }
                break;
            case 6:
                ran = (int) (Math.random()*89)+10;
                while(ran == 66 || ran == 75) {
                    ran = (int) (Math.random() * 89) + 10;
                }
                break;
        }
    }

    public void Btn1AnsCheck(){
        switch(iLevel){
            case 1:
                colPiScore++;
                break;
            case 2:
                colBlScore++;
                break;
            case 3:
                colPiScore++;
                break;
            case 4:
                norScore++;
                break;
            case 5:
                norScore++;
                break;
            case 6:
                colBlScore++;
                break;
        }
    }
    public void Btn2AnsCheck(){
        switch(iLevel){
            case 1:
                norScore++;
                break;
            case 2:
                norScore++;
                break;
            case 3:
                colBlScore++;
                break;
            case 4:
                colBlScore++;
                break;
            case 5:
                colBlScore++;
                break;
            case 6:
                colBlScore++;
                break;
        }
    }
    public void Btn3AnsCheck(){
        switch(iLevel){
            case 1:
                colBlScore++;
                break;
            case 2:
                colPiScore++;
                break;
            case 3:
                norScore++;
                break;
            case 4:
                colPiScore++;
                break;
            case 5:
                colBlScore++;
                break;
            case 6:
                norScore++;
                break;
        }
    }
    public void results(){
        s = (norScore>colPiScore && norScore>colBlScore) ? "정상" : (colPiScore>colBlScore)? "색약":"색맹";
    }
    public void testEnd(){
        setContentView(R.layout.colorpills_end);

        textView = (TextView)findViewById(R.id.textView5);
        exitBtn = (ImageButton) findViewById(R.id.home_btn);

        ansBtn1.setVisibility(View.GONE);
        ansBtn2.setVisibility(View.GONE);
        ansBtn3.setVisibility(View.GONE);

        results();

        textView.setText("당신은 "+ s + "입니다");
        textView.setVisibility(View.VISIBLE);
        exitBtn.setVisibility(View.VISIBLE);

        exitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
}
