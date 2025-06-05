package com.example.tenplants;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
        //프로그래밍 기획
        //[구성] 레이아웃 구성
        //[구성] 스토리 파일 - 게임 초기, 게임 엔딩
        //[기능] story.xml로 이동(이동 애니메이션 참고 https://oscarstory.tistory.com/118)
        //[기능] 버튼 클릭했을 떄 스토리, 이미지 넘어가기
        //      +넘어갈 때 fade in fade out 넣을까(걍 하지마)
        //[기능] 스토리, 이미지 자동 넘어가기(참고 https://wonit.tistory.com/171)

        //뭐시기.setText(string이름[][]);

        //가든에서 넘긴 인텐트 받기
        Intent endIntent = getIntent();
        //소스정보받기, 가든일떄만 최종점수 받기
        String source = endIntent.getStringExtra("source");
        if ("garden".equals(source)) {
        int endingID = endIntent.getIntExtra("storyType", 1); // 두 번째 인자는 기본값
            Log.e("endingID","endingID: " + endingID);
        // 점수에 따라 다른 엔딩임 참고하셈
        //TextView endingText = findViewById(R.id.ending_text); //텍스트뷰 있으면 연결
        //if (totalScore >= 270) {
        //    endingText.setText("전설의 정원사");
        //} else if (totalScore >= 200) {
        //    endingText.setText("열정의 정원사");
        //} else {
        //    endingText.setText("평범한 정원사");
        //}

        // endingID 따라 storyType 변경처리
            if(endingID == 1){
                storyType = 1;
            }else if (endingID == 2) {
                storyType = 2;
            }
            else if(endingID == 3){
                storyType = 3;
            }
            else{storyType = 1;}

        } else {
            // Main에서 왔을 경우 다른 처리
            Log.d("StoryManager", "MainActivity에서 온 진입. 점수는 없음.");
        }



        //mainActivity에서 intent 될 때 화면 깨짐 현상 있어서 해결
        new Handler(Looper.getMainLooper()).post(() -> {
            showStory(storyType, storyNum);
        });

        //게임 스토리 bgm
        SoundManager.playBGM("story");

        //스토리 다음으로 넘어가기
        ((Button)findViewById(R.id.story_btn_next)).setOnClickListener(v -> {
            storyNum++;
            if (storyNum < storyText[storyType].length) {
                // 아직 스토리가 남아있으면 다음 줄 보여주기
                showStory(storyType, storyNum);
            } else {
                // 스토리 끝났으면 메인으로 이동
                Intent intent = new Intent(StoryManager.this, MainActivity.class);
                if(storyType == 0){
                    intent.putExtra("message", "select");
                }
                else{
                    intent.putExtra("toTitle","title");
                }
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

//    public void storyType(int s_type) {
//        //s_type = 스토리 종류 값 받기
//        //start = 0
//        //엔딩 보통 = 1
//        //엔딩 굿 = 2
//        //엔딩 퍼펙트 = 3
//        storyType = s_type;
//        showStory(storyType, storyNum);
//    }

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
            { "식물 기르는 것을 좋아하는 당신은 어느날 기쁜 제안을 받았습니다.",
                    "그건 바로 꽃집을 운영해달라는 것!",
                    "당신은 기쁘게 제안을 수락하고 꽃집을 운영할 준비를 시작합니다.",
                    "꿈꾸던 정원사가 되기 위해선 어떤 식물도 멋지게 기를 수 있어야 합니다.",
                    "시작은.. 우선 씨앗부터 심어야겠죠!"
            },
            //여기서부턴 엔딩
            //쏘쏘
            { "축하합니다! 10개의 식물을 완전히 기르는 데 성공했습니다!",
                    "평범해 보이는 식물들이지만 이렇게 기르는데도 많은 시간과 정성이 들었습니다.",
                    "당신은 평범한 정원사지만 당신의 노력은 모두가 알아줄 겁니다!"
            },
            //굿
            { "축하합니다! 10개의 식물을 완전히 기르는 데 성공했습니다!",
                    "잘 키운 좋은 품질의 식물들이 햇살에 건강하게 빛납니다.",
                    "당신은 열정의 정원사, 당신의 열정이 이 식물들을 키웠습니다."
            },
            //퍼펙트
            { "축하합니다! 10개의 식물을 완전히 기르는 데 성공했습니다!",
                    "당신의 손에서 가꾸어진 식물들은 흠잡을 곳 없이 완벽합니다..!",
                    "당신은 전설의 정원사입니다. 당신이 키우지 못할 식물은 존재하지 않습니다."
            }
    };


    //스토리 background image
    public static int[] storyImage = {
            R.drawable.story_background, //시작
            R.drawable.story_ending_1, //보통
            R.drawable.story_ending_2, //열정
            R.drawable.story_ending_3 //전설

    };
}