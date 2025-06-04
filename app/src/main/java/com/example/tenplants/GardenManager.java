package com.example.tenplants;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

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
        finalAchievementScoreTextView = findViewById(R.id.finalAchievementScoreTextView);
        Button currentPlantButton = findViewById(R.id.current_plant);
        var blind = findViewById(R.id.blind);
        blind.setVisibility(View.INVISIBLE);

        updateCurrentEnergyDisplay(); // 처음 앱 열 때 기력 표시
        //startUpdatingRecoveryTime(); // 회복 시간 업데이트 시작
        updateFinalAchievementScoreDisplay(); //플레이어의 성취도 업데이트


        gameManager.setOnPlantGrowthListener(new MyGameManager.OnPlantGrowthListener() {
            @Override
            public void onGrowthComplete(String plantName, int finalAchievementScore) {
                runOnUiThread(() -> {
                    Toast.makeText(GardenManager.this, plantName + " 성장이 완료되었습니다!", Toast.LENGTH_SHORT).show();

                    // 성취도 점수 갱신
                    finalAchievementScoreTextView.setText(String.valueOf(finalAchievementScore));
                    new Thread(() -> {
                    // 완료된 식물 표시 등 추가 처리 가능
                    displayCompletedPlantsAndScore();

                    // 엔딩 체크도 가능 (엔딩 조건에 따라 따로 메서드 작성)
                    checkGameEndingCondition();
                    }).start();
                });
            }
        });

        ((Button) findViewById(R.id.energyreset)).setOnClickListener(v -> {
            dbHelper.setPlayerEnergyToMax(1);
        });
        // 씨앗 버튼
        ((Button) findViewById(R.id.seed)).setOnClickListener(v -> {
            // seed선택창 표시, blind 표시
            blind.setVisibility(View.VISIBLE);
            Log.i("씨앗", "심기창");
        });
        // 씨앗심기 버튼
        ((Button) findViewById(R.id.close_seed_selection)).setOnClickListener(v -> {
            // seed선택창 닫기, blind 내림
            blind.setVisibility(View.INVISIBLE);
            Log.i("씨앗", "심기창닫기");
        });

        ((Button) findViewById(R.id.oneseed)).setOnClickListener(v -> {
            AlertDialog.Builder seedAlertBuilder = new AlertDialog.Builder(this);
            seedAlertBuilder.setTitle(" ");
            seedAlertBuilder.setMessage("씨앗을 심으시겠습니까?");
            seedAlertBuilder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 씨앗심기
                    int result = 0;
                    gameManager.updateCurrentPlant(GardenManager.this, "Rose", result);
                    if (result == 1 || result == 0) {
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
        ((Button) findViewById(R.id.current_plant)).setOnClickListener(v -> {
            String plantName = dbHelper.getCurrentPlantName(); // 현재 식물 이름 가져오기
            int growth = gameManager.getCurrentPlantGrowth(); // 성장도 가져오기
            int step = gameManager.getCurrentPlantStep(); // 현재 성장 단계 가져오기
            //int achievement = gameManager.getAchieveScore(); // 성취도 점수 가져오기
            int achievement = gameManager.finalAchievementScore(); // 성취도 점수 가져오기
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

        //쓰다듬기 버튼
        ((Button) findViewById(R.id.hand)).setOnClickListener(v -> {
            if (useEnergy(1)) {   //기력 1 사용 및 식물 확인
                gameManager.growPlant(1);
            } else {
//                Toast.makeText(GardenManager.this,
//                        "현재 키우고 있는 식물이 없습니다!", Toast.LENGTH_SHORT).show();
            }
            //애니메이션
            //애니메이션 끝나면 엔딩체크
        });
        // 먼지 털기 버튼
        ((Button) findViewById(R.id.dust)).setOnClickListener(v -> {
            if (useEnergy(1)) {   //기력 1 사용 및 식물 확인
                new Thread(() -> {
                gameManager.growPlant(1);
                }).start();
            } else {
//                Toast.makeText(GardenManager.this,
//                        "현재 키우고 있는 식물이 없습니다!", Toast.LENGTH_SHORT).show();
            }

        });
        // 광합성 버튼
        ((Button) findViewById(R.id.light)).setOnClickListener(v -> {
            if (useEnergy(2)) {   //기력 2 사용 및 식물 확인
                new Thread(() -> {
                    gameManager.growPlant(2);
                }).start();
            } else {
//                Toast.makeText(GardenManager.this,
//                        "현재 키우고 있는 식물이 없습니다!", Toast.LENGTH_SHORT).show();
            }

        });
        // 물주기 버튼
        ((Button) findViewById(R.id.water)).setOnClickListener(v -> {
            if (useEnergy(5)) {   //기력 5 사용 및 식물 확인
                new Thread(() -> {
                    gameManager.growPlant(5);
                }).start();
            } else {
//                Toast.makeText(GardenManager.this,
//                        "현재 키우고 있는 식물이 없습니다!", Toast.LENGTH_SHORT).show();
            }

        });
        // 비료 주기 버튼
        ((Button) findViewById(R.id.fertilizer)).setOnClickListener(v -> {
            if (useEnergy(10)) {   //기력 10 사용 및 식물 확인
                new Thread(() -> {
                    gameManager.growPlant(10);
                }).start();
            } else {
//                Toast.makeText(GardenManager.this,
//                        "현재 키우고 있는 식물이 없습니다!", Toast.LENGTH_SHORT).show();
            }

        });
        // 노래 불러 주기 버튼
        ((Button) findViewById(R.id.sing)).setOnClickListener(v -> {
            if (useEnergy(15)) {   //기력 15 사용 및 식물 확인
                new Thread(() -> {
                    gameManager.growPlant(15);
                }).start();
            } else {

            }
            //애니메이션
            //애니메이션 끝나면 엔딩체크

        });
        ((Button) findViewById(R.id.completeplant)).setOnClickListener(v -> {
            displayCompletedPlantsAndScore();
        });
    }

    private void checkGameEndingCondition() {
        int completedCount = dbHelper.getCompletedPlantCount();

        // 예시: 모든 10개 식물을 완성하면 엔딩 액티비티로 이동
        if (completedCount >= 10) {
            // ✅ 반드시 메인 스레드에서 실행
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(this, "모든 식물을 완성했습니다! 엔딩으로 이동합니다.", Toast.LENGTH_LONG).show();
                gameManager.checkAndShowEndingAsync(this, dbHelper.getWritableDatabase());
            });
        }
    }
    public void displayCompletedPlantsAndScore() {
        new Thread(() -> {
        List<String> completedPlantNames = dbHelper.getCompletedPlantNames();
        int achievementScore = dbHelper.getFinalAchievementScore();

        Log.e("GardenActivityManager", "=== 완료된 식물 목록 ===");
        for (String name : completedPlantNames) {
            Log.e("GardenActivityManager", "완료된 식물: " + name);
        }
        Log.e("GardenActivityManager", "현재 성취도 점수: " + achievementScore);
        }).start();
    }

    public void OnClickupdatePlayerEnergy() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int newEnergy = 120;
        ContentValues values = new ContentValues();
        values.put("energy", newEnergy);

        db.update("PlayerData", values, "id = ?", new String[]{"1"});

    }

    //애니메이션 판단하고 작동
    private void actionAnima() {
        //쓰다듬기면 쓰다듬기 애니메이션, 성장도 오르는 애니메이션
    }


    //성취도&&엔딩
//    private void achievementCheck(){
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        int achieve =
//    }

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
            Toast.makeText(GardenManager.this,
                    "현재 키우고 있는 식물이 없습니다!", Toast.LENGTH_SHORT).show();
            return false;
        }
        int currentEnergy = gameManager.updateEnergyWithRecovery();// 최신 기력 계산

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
        runOnUiThread(() -> {
            currentEnergyTextView.setText(String.valueOf(currentEnergy));
        });
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
        if (currentEnergy != 120) {
            String timeText = String.format("%02d:%02d", minutes, seconds);
            recoveryTimeTextView.setText(timeText);

            currentEnergyTextView.setText(String.valueOf(currentEnergy)); // 기력도 함께 갱신
        } else if (currentEnergy == 120) {
            recoveryTimeTextView.setText("00:30");
            currentEnergyTextView.setText("120");
        }
    }

    private void updateFinalAchievementScoreDisplay() {
        int score = gameManager.getAchieveScore();
        //if (score != 0) {
        runOnUiThread(() -> {
        finalAchievementScoreTextView.setText(String.valueOf(score));
        });
        //}
    }

    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            updateRecoveryTime();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        startUpdatingRecoveryTime();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUpdatingRecoveryTime();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("PlantActivity", "onDestroy() 호출됨 - 액티비티가 종료됨");
    }


    private void stopUpdatingRecoveryTime() {
        handler.removeCallbacks(updateTask);
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        // 이전 액티비티에 상태 전달
        Intent storyIntent = new Intent();
        storyIntent.putExtra("select_content", true); // 패널을 VISIBLE로
        storyIntent.putExtra("source", "garden");
        setResult(RESULT_OK, storyIntent);
        finish(); // 현재 액티비티 종료
        return false;
    }
}