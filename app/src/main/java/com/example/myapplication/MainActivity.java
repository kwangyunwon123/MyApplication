package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    // 4개의 메뉴에 들어갈 Fragment들
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, StartActivity.class));
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        // 첫 화면 지정
        replaceFragment(Menu_Home.newInstance());

        //bottomNavigationView의 아이템이 선택될 때 호출될 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home: {
                        replaceFragment(Menu_Home.newInstance());
                        break;
                    }
                    case R.id.navigation_test: {
                        replaceFragment(Menu_Test.newInstance());
                        break;
                    }
                    case R.id.navigation_map: {
                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.navigation_face_recognition: {
                        replaceFragment(Menu_FaceRecognition.newInstance());
                        break;
                    }
                }
                return true;
            }
        });
    }
    public void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment).commit();
    }
}
