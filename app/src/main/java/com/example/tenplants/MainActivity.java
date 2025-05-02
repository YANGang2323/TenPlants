package com.example.tenplants;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
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

        var start_scene = findViewById(R.id.start_scene);
        var select_content = findViewById(R.id.select_content);
        var blind = findViewById(R.id.blind);
        start_scene.setVisibility(View.VISIBLE);
        select_content.setVisibility(View.INVISIBLE);
        blind.setVisibility(View.INVISIBLE);

        //새로시작 버튼
        ((Button)findViewById(R.id.game_start)).setOnClickListener(v -> {
            //초기화 확인 알림
            AlertDialog.Builder resetAlertBuilder = new AlertDialog.Builder(this);
            resetAlertBuilder.setTitle("새 게임을 시작할 시 모든 진행사항이 초기화됩니다.");
            resetAlertBuilder.setMessage("새 게임을 시작하시겠습니까?");
            resetAlertBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 초기화된 새 게임 시작, story 액티비티로 이동
                    Intent startStoryIntent = new Intent(MainActivity.this, StoryManager.class);
                    startActivity(startStoryIntent);
                }
            });
            resetAlertBuilder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog resetAlert = resetAlertBuilder.create();
            resetAlert.show();
        });
        //이어하기 버튼
        ((Button)findViewById(R.id.game_continue)).setOnClickListener(v -> {
            // 저장된 게임 이어서, select_content 표시
            start_scene.setVisibility(View.INVISIBLE);
            select_content.setVisibility(View.VISIBLE);
        });
        //옵션 버튼
        ((Button)findViewById(R.id.game_option)).setOnClickListener(v -> {
            // 옵션창 표시, blind 표시
            start_scene.setVisibility(View.VISIBLE);
            blind.setVisibility(View.VISIBLE);
        });
        ((Button)findViewById(R.id.close_option)).setOnClickListener(v -> {
            // 옵션창 닫기, blind 표시
            start_scene.setVisibility(View.VISIBLE);
            blind.setVisibility(View.INVISIBLE);
        });

        //content select button
        ((Button)findViewById(R.id.select_garden)).setOnClickListener(v -> {
            // 가든으로 가기
            Intent gardenIntent = new Intent(this, GardenManager.class);
            startActivity(gardenIntent);
        });

        ((Button)findViewById(R.id.select_collection)).setOnClickListener(v -> {
            // collectionRoom 가기
            Intent collectionRoomIntent = new Intent(this, CollectionRoomManager.class);
            startActivity(collectionRoomIntent);
        });



        //기력 불러오기

        //기력 자동증가

        //


    }


    //앱 생애주기 onresume


}