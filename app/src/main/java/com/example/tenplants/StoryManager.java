package com.example.tenplants;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StoryManager extends AppCompatActivity {     //류수민
    private MyGameManager gameManager;
    //프로그래밍 기획
    //[구성] 레이아웃 구성
    //[구성] 스토리 파일 - 게임 초기, 게임 엔딩
    //[기능] story.xml로 이동(이동 애니메이션 참고 https://oscarstory.tistory.com/118)
    //[기능] 버튼 클릭했을 떄 스토리, 이미지 넘어가기
    //      +넘어갈 때 fade in fade out 넣을까(걍 하지마)
    //[기능] 스토리, 이미지 자동 넘어가기(참고 https://wonit.tistory.com/171)

    //뭐시기.setText(string이름[][]);

    int storyType = 0;
    int storyNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story);

        //mainActivity에서 intent 될 때 화면 깨짐 현상 있어서 해결
        new Handler(Looper.getMainLooper()).post(() -> {
            showStory(storyType, storyNum);
        });

        //스토리 다음으로 넘어가기
        ((Button)findViewById(R.id.story_btn_next)).setOnClickListener(v -> {
            storyNum++;
            if (storyNum < storyText[storyType].length) {
                // 아직 스토리가 남아있으면 다음 줄 보여주기
                showStory(storyType, storyNum);
            } else {
                // 스토리 끝났으면 메인으로 이동
                Intent intent = new Intent(StoryManager.this, MainActivity.class);
                intent.putExtra("message", "select");
                startActivity(intent);
                finish();
            }
        });

        //스토리 스킵 버튼(mainActivity select_content로 이동)
        ((Button)findViewById(R.id.story_btn_skip)).setOnClickListener(v -> {
            Intent intent = new Intent(StoryManager.this, MainActivity.class);
            intent.putExtra("message", "select");
            startActivity(intent);
        });
    }

    public void storyType(int s_type) {
        //s_type = 스토리 종류 값 받기
        //start = 0
        //엔딩 보통 = 1
        //엔딩 굿 = 2
        //엔딩 퍼펙트 = 3
        storyType = s_type;
        showStory(storyType, storyNum);
    }

    public void showStory(int s_type, int s_num){ //스토리 보여줌

        //스토리 텍스트
        TextView s_text = (TextView)findViewById(R.id.story_text);
        if(s_num < storyText[s_type].length){
            s_text.setText(storyText[s_type][s_num]);
        }

        //스토리 이미지(배경사진) - 스토리 당 이미지 1개로 해놨는데 나중에 수정할거면 수정
        ImageView s_image = (ImageView)findViewById(R.id.story_image);
        s_image.setImageResource(storyImage[s_type]);
    }

    //스토리 text
    public static String[][] storyText = {
            //초기 스토리
            { "start_1",
                    "start_2",
                    "start_3",
                    "start_4"
            },
            //여기서부턴 엔딩
            //쏘쏘
            { "end1_1",
                    "end1_2"
            },
            //굿
            { "end2_1",
                    "end2_2"
            },
            //퍼펙트
            { "end3_1",
                    "end3_2"}
    };

    //스토리 background image
    public static int[] storyImage = {
            R.drawable.story_background,
            R.drawable.story_ending_1,
            R.drawable.story_ending_2,
    };
}
