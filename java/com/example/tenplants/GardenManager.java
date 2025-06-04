package com.example.tenplants;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class GardenManager extends AppCompatActivity {        //같이
    private MyGameManager gameManager;
    private TextView recoveryTimeTextView;
    private TextView currentEnergyTextView;
    private TextView finalAchievementScoreTextView;
    private Handler handler = new Handler();
    private GameDatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.garden);
        dbHelper = new GameDatabaseHelper(this);
        gameManager = new MyGameManager(this);
        recoveryTimeTextView = findViewById(R.id.recoveryTimeTextView);
        currentEnergyTextView = findViewById(R.id.currentEnergyTextView);
        //finalAchievementScoreTextView = findViewById(R.id.finalAchievementScoreTextView);
        Button currentPlantButton = findViewById(R.id.current_plant);
        var blind = findViewById(R.id.blind);
        blind.setVisibility(View.INVISIBLE);

        //애니메이션 로딩
        Animation growing = AnimationUtils.loadAnimation(this, R.anim.growing);

        updateCurrentEnergyDisplay(); // 처음 앱 열 때 기력 표시
        startUpdatingRecoveryTime(); // 회복 시간 업데이트 시작

        // 씨앗 버튼
        ((Button)findViewById(R.id.seed)).setOnClickListener(v -> {
            // seed선택창 표시, blind 표시
            blind.setVisibility(View.VISIBLE);
            Log.i("씨앗","심기창");
        });
        // 씨앗심기 버튼
        ((Button)findViewById(R.id.close_seed_selection)).setOnClickListener(v -> {
            // seed선택창 닫기, blind 내림
            blind.setVisibility(View.INVISIBLE);
            Log.i("씨앗","심기창닫기");
        });

        ((Button)findViewById(R.id.oneseed)).setOnClickListener(v -> {
            AlertDialog.Builder seedAlertBuilder = new AlertDialog.Builder(this);
            seedAlertBuilder.setTitle(" ");
            seedAlertBuilder.setMessage("씨앗을 심으시겠습니까?");
            seedAlertBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 씨앗심기
                    int result = 0;
                    gameManager.updateCurrentPlant(GardenManager.this, "Rose", result);
                    if(result == 1 || result == 0){
                        currentPlantButton.setText("Rose");
                    }
                    Log.i("씨앗", "심기");

                }
            });
            seedAlertBuilder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 닫기
                }
            });
            AlertDialog seedAlert = seedAlertBuilder.create();
            seedAlert.show();
        });

        //현재식물의 성장도확인 버튼
        ((Button)findViewById(R.id.current_plant)).setOnClickListener(v -> {
            String plantName = dbHelper.getCurrentPlantName(); // 현재 식물 이름 가져오기
            int growth = gameManager.getCurrentPlantGrowth(); // 성장도 가져오기
            int step = gameManager.getCurrentPlantStep(); // 현재 성장 단계 가져오기
            int achievement = gameManager.getCurrentPlantAchievement(); // 성취도 점수 가져오기
            String grade = gameManager.getCurrentPlantGrade(); // 등급 가져오기

            if (growth == -1) { // 오류 발생 시
                new AlertDialog.Builder(GardenManager.this)
                                .setTitle("오류 발생")
                                .setMessage("현재 키우고 있는 식물이 없습니다!")
                                .setPositiveButton("확인", null)
                                .show();
                Log.e("PlantCheck", "현재 키우고 있는 식물이 없음.");

            } else {
                String message = "🌱 식물 이름: " + plantName +
                        "\n📊 등급: " + grade +
                        "\n🔼 성장도: " + growth +
                        "\n🪴 단계: " + step +
                        "\n🏆 성취도: " + achievement;
                AlertDialog.Builder seedAlertBuilder = new AlertDialog.Builder(this);
                seedAlertBuilder.setTitle("현재 식물 정보");
                seedAlertBuilder.setMessage(message);
                seedAlertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog seedAlert = seedAlertBuilder.create();
                seedAlert.show();
            }
        });

        //애니메이션 적용할 식물 선언
        ImageView plant;
        plant = findViewById(R.id.plant);

            //쓰다듬기 버튼
            ((Button) findViewById(R.id.hand)).setOnClickListener(v -> {
                    if(useEnergy(1)) {   //기력 1 사용 및 식물 확인
                        gameManager.growPlant(1);
                        plant.startAnimation(growing); //성장 애니메이션
                    } else{Toast.makeText(GardenManager.this, "현재 키우고 있는 식물이 없습니다!", Toast.LENGTH_SHORT).show();}
            });
            // 먼지 털기 버튼
            ((Button) findViewById(R.id.dust)).setOnClickListener(v -> {
                if(useEnergy(1)) {   //기력 1 사용 및 식물 확인
                    gameManager.growPlant(1);
                    plant.startAnimation(growing); //성장 애니메이션
                } else{Toast.makeText(GardenManager.this, "현재 키우고 있는 식물이 없습니다!", Toast.LENGTH_SHORT).show();}
            });
            // 광합성 버튼
            ((Button) findViewById(R.id.light)).setOnClickListener(v -> {
                if(useEnergy(2)) {   //기력 2 사용 및 식물 확인
                    gameManager.growPlant(2);
                    plant.startAnimation(growing); //성장 애니메이션
                } else{Toast.makeText(GardenManager.this, "현재 키우고 있는 식물이 없습니다!", Toast.LENGTH_SHORT).show();}
            });
            // 물주기 버튼
            ((Button) findViewById(R.id.water)).setOnClickListener(v -> {
                if(useEnergy(5)) {   //기력 5 사용 및 식물 확인
                    gameManager.growPlant(5);
                    plant.startAnimation(growing); //성장 애니메이션
                } else{Toast.makeText(GardenManager.this, "현재 키우고 있는 식물이 없습니다!", Toast.LENGTH_SHORT).show();}
            });
            // 비료 주기 버튼
            ((Button) findViewById(R.id.fertilizer)).setOnClickListener(v -> {
                if(useEnergy(10)) {   //기력 10 사용 및 식물 확인
                    gameManager.growPlant(10);
                    plant.startAnimation(growing); //성장 애니메이션
                } else{Toast.makeText(GardenManager.this, "현재 키우고 있는 식물이 없습니다!", Toast.LENGTH_SHORT).show();}
            });
            // 노래 불러 주기 버튼
            ((Button) findViewById(R.id.sing)).setOnClickListener(v -> {
                if(useEnergy(15)) {   //기력 15 사용 및 식물 확인
                    gameManager.growPlant(15);
                    plant.startAnimation(growing); //성장 애니메이션
                } else{Toast.makeText(GardenManager.this, "현재 키우고 있는 식물이 없습니다!", Toast.LENGTH_SHORT).show();}
            });

    }

    // 기력 소모
    public boolean useEnergy(int amount) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int growth = gameManager.getCurrentPlantGrowth(); // 성장도 가져오기
                if (growth == -1) { // 오류 발생 시
//                    new AlertDialog.Builder(GardenManager.this)
//                            .setTitle("오류 발생")
//                            .setMessage("현재 키우고 있는 식물이 없습니다!")
//                            .setPositiveButton("확인", null)
//                            .show();
                    Log.e("PlantCheck", "현재 키우고 있는 식물이 없음.");
                    return false;
                }
        int currentEnergy = gameManager.updateEnergyWithRecovery();// 최신 기력 계산
        db.close();
        if (currentEnergy >= amount) {
            gameManager.saveEnergy(currentEnergy - amount);
            return true;
        } else {
            new AlertDialog.Builder(GardenManager.this)
                    .setMessage("기력이 부족합니다!")
                    .setPositiveButton("확인", null)
                    .show();
            Log.i("기력", "기력 부족");
            return false;
        }
    }

    private void updateCurrentEnergyDisplay() {
        int currentEnergy = gameManager.updateEnergyWithRecovery();
        currentEnergyTextView.setText(String.valueOf(currentEnergy));
    }
    //UI에서 회복까지 남은 시간 표시 (남은 시간 계산)
    private void startUpdatingRecoveryTime() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateRecoveryTime();
                handler.postDelayed(this, 1000); // 1초마다 업데이트
            }
        }, 1000); // 첫 번째 실행 지연
    }
    private void updateRecoveryTime() {
        int currentEnergy = gameManager.updateEnergyWithRecovery();
        long remainingTime = gameManager.getTimeUntilNextRecovery();
        long seconds = remainingTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        // UI에 남은 시간 표시
        if(currentEnergy != 120) {
            String timeText = String.format("%02d:%02d", minutes, seconds);
            recoveryTimeTextView.setText(timeText);

            currentEnergyTextView.setText(String.valueOf(currentEnergy)); // 기력도 함께 갱신
        }else if(currentEnergy == 120){
            recoveryTimeTextView.setText("00:30");
            currentEnergyTextView.setText("120");
        }
    }
    private void updateFinalAchievementScoreDisplay() {
        int score = gameManager.finalAchievementScore();
        finalAchievementScoreTextView.setText(String.valueOf(score));
    }

    // 뒤로가기 눌렀을 때 mainActivity select_content 창으로 이동
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(GardenManager.this, MainActivity.class);
            intent.putExtra("message", "select");
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}