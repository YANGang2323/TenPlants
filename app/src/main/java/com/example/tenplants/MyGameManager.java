package com.example.tenplants;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

// MyGameManager 클래스
public class MyGameManager {

    public static final int MAX_ENERGY = 120;
    public static final int RECOVERY_INTERVAL_MS = 30 * 1000; // 30초당 1회복

    private GameDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;
    private long lastUpdateTime;

    public MyGameManager(Context context) {
        dbHelper = new GameDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        this.context = context;
    }
    private MyGameManager() {}
    private static MyGameManager instance;
    public static synchronized MyGameManager getInstance(Context context) {
        if (instance == null) {
            instance = new MyGameManager(context);
        }
        return instance;
    }
    private List<String> grownPlants = new ArrayList<>();
    public void addGrownPlant(String plantName) {
        if (!grownPlants.contains(plantName)) {
            grownPlants.add(plantName);
        }
    }
    //27번째 줄부터 여기까지 성장 완료한 식물 추가
    // (collectionRoom에서 여기 데이터 인식해서 식물 추가)

    public List<String> getGrownPlants() {
        return new ArrayList<>(grownPlants); // 복사본 반환 (불변성 유지)
    }
    //여기까지
    //생성자 바꾸지 말고 getInstance(this)로 호출하기 , 밑에 grow함수도 그대로 쓰고





    // 기력 회복 포함한 최신 기력 불러오기
    public int updateEnergyWithRecovery() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int energy = 0;
        long lastUpdateTime = System.currentTimeMillis();

        Cursor cursor = db.rawQuery("SELECT energy, lastUpdateTime FROM PlayerData WHERE id = 1", null);
        boolean hasData = cursor.moveToFirst();
        if (hasData) {
            energy = cursor.getInt(0);
            lastUpdateTime = cursor.getLong(1);
        }
        cursor.close();
        long now = System.currentTimeMillis();
        // 데이터가 아예 없던 경우: 초기화
        if (!hasData) {
            energy = MAX_ENERGY;
            ContentValues initValues = new ContentValues();
            initValues.put("id", 1);
            initValues.put("energy", energy);
            initValues.put("lastUpdateTime", now);
            initValues.put("finalAchievementScore", 0); // 이게 빠지면 null로 들어감

            //db.insert("PlayerData", null, initValues);
            db.update("PlayerData", initValues, "id = ?", new String[]{"1"});
            return energy;
        }
        // 회복 로직
        long elapsed = now - lastUpdateTime;
        int recovered = (int) (elapsed / RECOVERY_INTERVAL_MS);

        if (recovered > 0) {
            energy = Math.min(MAX_ENERGY, energy + recovered);
            long remainder = elapsed % RECOVERY_INTERVAL_MS;

            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("energy", energy);
            values.put("lastUpdateTime", energy < MAX_ENERGY ? now - remainder : now);

            db.insertWithOnConflict("PlayerData", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }

        return energy;
    }

    // 강제로 저장 (회복 포함 X)
    public void saveEnergy(int energy) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int currentScore = getAchieveScore();//성취도 유지
        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("energy", energy);
        values.put("lastUpdateTime", System.currentTimeMillis());
        values.put("finalAchievementScore", currentScore);
        db.insertWithOnConflict("PlayerData", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // 기력 회복 남은 시간 계산
    public long getTimeUntilNextRecovery() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT lastUpdateTime FROM PlayerData WHERE id = 1", null);
        long lastUpdateTime = System.currentTimeMillis();

        if (cursor.moveToFirst()) {
            lastUpdateTime = cursor.getLong(0);
        }
        cursor.close();

        long now = System.currentTimeMillis();
        long elapsed = now - lastUpdateTime;
        long remainingTime = RECOVERY_INTERVAL_MS - (elapsed % RECOVERY_INTERVAL_MS);

        return remainingTime;
    }


    //성장도 로직
    //씨앗심고 현재식물 업데이트
    public int updateCurrentPlant(Context context, String name, int result){
        Cursor cursor = dbHelper.getCurrentPlants();

        if(cursor.getCount() <= 0) {
            int grade;
            switch (name) {
                case "Rose":
                case "ardisia_pusilla":
                case "ficus_pumila":
                case "sansevieria":
                    grade = 0; // BASIC 초급
                    break;
                case "geranium_palustre":
                case "kerria_japonica":
                case "trigonotis_peduncularis":
                case "eglantine":
                case "narcissus":
                    grade = 1; // INTERMEDIATE 중급
                    break;
                case "coreopsis_basalis":
                case "lavandula_angustifolia":
                case "pansy":
                case "rhododendron_schlippenbachii":
                    grade = 2; // ADVANCED 고급
                    break;
                default:
                    Log.i("씨앗", "해당없음");
                    return result; // 식물 이름이 잘못된 경우 실행 중지
            }

            // 성장 제한 가져오기
            int maxGrowth = dbHelper.getMaxGrowth(name);

            // 데이터베이스에 삽입 (Plant 클래스 없이)
            dbHelper.insertCurrentPlant(name, grade, 0, 0, maxGrowth);
            Log.i("씨앗", name + " 심기 완료");
            cursor.close(); result = 1; return result;
        }else{
            AlertDialog.Builder seedDuplicateAlertBuilder = new AlertDialog.Builder(context);
            seedDuplicateAlertBuilder.setTitle(" ");
            seedDuplicateAlertBuilder.setMessage("키우고 있는 식물이 있으므로 심을 수 없습니다.");
            seedDuplicateAlertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //닫기
                }
            });
            AlertDialog seedDuplicateAlert = seedDuplicateAlertBuilder.create();
            seedDuplicateAlert.show();
            cursor.close(); result = 0;
            return result;
        }
    }



    //    // 기력 소모 후 성장도 업데이트 메서드
    public interface OnPlantGrowthListener {
        void onGrowthComplete(String plantName, int finalAchievementScore);
    }

    private OnPlantGrowthListener growthListener;

    public void setOnPlantGrowthListener(OnPlantGrowthListener listener) {
        this.growthListener = listener;
    }


    public void growPlant(int addGrowth) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String name = dbHelper.getCurrentPlantName();
        Cursor cursor = db.rawQuery("SELECT growth, maxGrowth, grade, step FROM CurrentPlants WHERE name = ?",
                new String[]{name});

        if (cursor.moveToFirst()) {
            int currentGrowthIndex = cursor.getColumnIndex("growth");
            int currentGrowth = cursor.getInt(currentGrowthIndex);
            int maxGrowthIndex = cursor.getColumnIndex("maxGrowth");
            int maxGrowth = cursor.getInt(maxGrowthIndex);
            int gradeIndex = cursor.getColumnIndex("grade");
            int grade = cursor.getInt(gradeIndex);
            int stepIndex = cursor.getColumnIndex("step");
            int step = cursor.getInt(stepIndex);

            // 성장도 증가 (초과 성장 시 초과분 이월)
            int newGrowth = currentGrowth + addGrowth;
            if (newGrowth > maxGrowth) {
                newGrowth = maxGrowth;  //최대성장
            }
            int maxGrowth2 = maxGrowth;
            int newGrowth2 = newGrowth;
            int newStep = dbHelper.calculatePlantStep(newGrowth, grade);

            if (newGrowth2 >= maxGrowth2) {
                //식물 성장 완료
                SoundManager.playSFX("garden_plant_grow_max");
                // 성장 완료 처리 (CompletePlants 테이블로 이동)
                ContentValues completedValues = new ContentValues();
                completedValues.put("name", name);
                completedValues.put("completedTime", System.currentTimeMillis());
                completedValues.put("grade", grade);
                db.insert("CompletedPlants", null, completedValues);

                // 현재 식물 삭제
                db.delete("CurrentPlants", "name = ?", new String[]{name});
                Log.i("PlantGrowth", name + "가 최대 성장에 도달하여 완료됨!");

                dbHelper.accumulateAchievement(grade);

                if (growthListener != null) {
                    growthListener.onGrowthComplete(name, dbHelper.getFinalAchievementScore());
                }
            } else {
                // 성장도 업데이트
                ContentValues updateValues = new ContentValues();
                updateValues.put("growth", newGrowth2);
                updateValues.put("step", newStep);
                db.update("CurrentPlants", updateValues, "name = ?", new String[]{name});
                Log.i("PlantGrowth", name + "의 성장도가 " + newGrowth2 + "으로 업데이트됨, 단계: " + newStep);
            }
        }
        cursor.close();

    }

    public int getCurrentPlantGrowth() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT growth FROM CurrentPlants LIMIT 1", null);
        int growth = -1; // 기본값: 오류 발생 시 -1 반환

        if (cursor.moveToFirst()) {
            int growthIndex = cursor.getColumnIndex("growth");
            growth = cursor.getInt(growthIndex);
        }
        cursor.close();
        return growth; // 현재 식물의 성장도 반환
    }


    //엔딩 확인 및 처리
    public void checkAndShowEndingAsync(Context context, SQLiteDatabase db) {
        new Thread(() -> {
            Cursor cursor = db.rawQuery("SELECT grade FROM CompletedPlants", null);
            final int[] totalScore = {0};  // 점수 누적용
            final int[] endingID = {1}; // 배열로 감싸기

            while (cursor.moveToNext()) {
                int grade = cursor.getInt(0);
                int score = 0;

                switch (grade) {
                    case 0: score = 10; break;  // 초급
                    case 1: score = 20; break;  // 중급
                    case 2: score = 30; break;  // 고급
                    default:
                        Log.w("엔딩계산", "알 수 없는 grade: " + grade);
                        break;
                }

                totalScore[0] += score;
                Log.d("엔딩계산", "grade: " + grade + " → score: " + score);
            }

            cursor.close();

            String ending;
            String storyID;
            if (totalScore[0] >= 270) {ending = "전설의 정원사 엔딩"; endingID[0] = 3;}
            else if (totalScore[0] >= 200) {ending = "열정의 정원사 엔딩"; endingID[0] = 2;}
            else {ending = "평범한 정원사 엔딩"; endingID[0] = 1;}
            storyID = Integer.toString(endingID[0]);

            Log.e("엔딩결정", "최종점수: " + totalScore[0] + ", 엔딩: " + ending + ", 엔딩번호" + storyID);

            // DB에 엔딩 정보 저장
            ContentValues values = new ContentValues();
            values.put("ending", ending);
            db.insert("UnlockedEndings", null, values);
            //엔딩으로 이동 Sound
            SoundManager.playSFX("garden_game_clear");
            // UI 스레드에서 엔딩 액티비티 전환
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                Intent endStory  = new Intent(context, StoryManager.class);
                endStory.putExtra("source", "garden");
                endStory.putExtra("storyType", endingID[0]);
                context.startActivity(endStory);
            });
        }).start();
    }

    public int getCurrentPlantStep() {
        return dbHelper.getCurrentPlantStep();
    }
    public int getAchieveScore() { return dbHelper.getPlayerScoreData(); }
    public String getCurrentPlantGrade() {
        return dbHelper.getCurrentPlantGrade();
    }
    public int finalAchievementScore() {
        return dbHelper.getFinalAchievementScore();
    }

}