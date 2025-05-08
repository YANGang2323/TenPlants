package com.example.tenplants;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private GameManager gameManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main_title);

        gameManager = new GameManager(this);


        //기력 불러오기

        //기력 자동증가

        //타이틀 디테일 - 초기화(alert)
        //인텐트(메인에서 스토리로)
        ((Button)findViewById(R.id.game_start)).setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "게임을 초기화합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, StoryManager.class);
            startActivity(intent);
        });


    }
}