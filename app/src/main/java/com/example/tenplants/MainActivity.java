package com.example.tenplants;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
//만들 텍스트.자바 파일목록
//스토리 스크립트, 식물이름+설명들,
public class MainActivity extends AppCompatActivity {      //둘이 같이, 함수 정의옆에 이름쓰기
    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main_title);

        gameManager = new GameManager(this);


        //기력 불러오기

        //기력 자동증가

        //


    }


    //앱 생애주기 onresume


}