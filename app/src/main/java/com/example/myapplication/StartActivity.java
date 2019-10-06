package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class StartActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_main);

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
}