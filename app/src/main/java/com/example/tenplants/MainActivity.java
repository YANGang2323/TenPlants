package com.example.tenplants;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
//만들 텍스트.자바 파일목록
//스토리 스크립트, 식물이름+설명들
public class MainActivity extends AppCompatActivity {      //둘이 같이, 함수 정의옆에 이름쓰기
    private GameManager gameManager;

    //(완료) 타이틀 디테일 - 초기화(alert)
    //(완료) 사진 이름 수정 - lv_식물이름
    //가든 전시대(fragment)
    //스토리 디테일(애니메이션)
    //(완료) 리소스(배경, 화분, 버튼)
    //깃허브 2에 올리기, 빠진 거 없이 올리기

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


    //앱 생애주기 onresume


}