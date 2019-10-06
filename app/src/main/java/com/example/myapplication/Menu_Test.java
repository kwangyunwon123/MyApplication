package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.widget.ImageButton;

public class Menu_Test extends Fragment {
    public static Menu_Test newInstance() {
        return new Menu_Test();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_menu,
                container, false);

        ImageButton button = (ImageButton) view.findViewById(R.id.testStart);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainActivity)getActivity()).replaceFragment(WaitFragment.newInstance());
            }
        });

        ImageButton button2 = (ImageButton)view.findViewById(R.id.testStart2);
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity().getApplicationContext(), ColorpillsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
