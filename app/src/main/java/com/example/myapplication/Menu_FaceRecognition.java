package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Menu_FaceRecognition extends Fragment{
    public static Menu_FaceRecognition newInstance() {
        return new Menu_FaceRecognition();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(getActivity().getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        }, 3000);
        return inflater.inflate(R.layout.face_recognition_menu, container, false);
    }
}