package com.example.tenplants;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

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

    private static MyGameManager instance;

    private List<String> grownPlants = new ArrayList<>();

    private MyGameManager() {}

    public static MyGameManager getInstance() {
        if (instance == null) {
            instance = new MyGameManager();
        }
        return instance;
    }

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


    public MyGameManager(Context context) {
        dbHelper = new GameDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        this.context = context;
    }


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
            db.insert("PlayerData", null, initValues);
            db.close();
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

        db.close();
        return energy;
    }

    // 강제로 저장 (회복 포함 X)
    // ✅ 기력 저장
    public void saveEnergy(int energy) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("energy", energy);
        db.insertWithOnConflict("PlayerData", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // 단순 조회 (회복 포함 X)
    // ✅ 기력 불러오기
    public int getEnergy() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT energy FROM PlayerData WHERE id = 1", null);
        int energy = 0;
        if (cursor.moveToFirst()) {
            energy = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return energy;
    }
    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
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

        db.close();
        return remainingTime;
    }
    public void initEnergyIfNeeded() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM PlayerData WHERE id = 1", null);
        boolean hasData = cursor.moveToFirst();
        cursor.close();

            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("energy", MAX_ENERGY);
            values.put("lastUpdateTime", System.currentTimeMillis());
            db.insert("PlayerData", null, values);


        db.close();
    }


    //성장도 로직
    //씨앗심고 현재식물 업데이트
    //insertCurrentPlant(int id, String name, int grade, int growth, int maxGrowth)
    //grade 0: BASIC, 1: INTERMEDIATE, 2: ADVANCED
    public int updateCurrentPlant(Context context, String name, int result){
        Cursor cursor = dbHelper.getCurrentPlants();

        if(cursor.getCount() <= 0) {
            int grade;
            switch (name) {
                case "Rose":
                case "Tulip":
                    grade = 0; // BASIC
                    break;
                case "Cactus":
                    grade = 1; // INTERMEDIATE
                    break;
                case "Orchid":
                    grade = 2; // ADVANCED
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
public void growPlant(int growthAmount) {
    dbHelper.growPlant(growthAmount);
}
//    public void growPlant(int growthAmount) {
//        String plantName = dbHelper.getCurrentPlantName(); // 현재 키우고 있는 식물의 이름 가져오기
//        if (plantName != null) { // 식물이 존재하는 경우에만 성장 업데이트
//            SQLiteDatabase db = dbHelper.getWritableDatabase();
//            db.execSQL("UPDATE CurrentPlants SET growth = growth + ? WHERE name = ?",
//                    new Object[]{growthAmount, plantName});
//            db.close();
//        }else{
//            Toast.makeText(context, "먼저 식물을 심어주세요.", Toast.LENGTH_LONG).show();
//        }
//    }
//    private int updatePlantGrowth(int currentGrowth, int addGrowth, int grade, String plantName) {
//        int MaxGrowth = getMaxGrowthForGrade(plantName, grade);
//        // 각 단계에 맞는 최대 성장도 (최대치 도달 시 단계를 올려야 함)
//        if (currentGrowth >= MaxGrowth) {
//            currentGrowth = currentGrowth - MaxGrowth;  // 성장도 초기화
//        }else {currentGrowth += addGrowth;}
//        return currentGrowth;
//    }


    // 성장도의 최대치 반환 메서드
    // 등급에 따라 최대 성장도 반환
    private int getMaxGrowthForGrade(String name, int grade) {
        // 간단한 예시 (식물 이름과 무관하게 등급만 고려)
        switch (grade) {
            case 0: return 10;
            case 1: return 20;
            case 2: return 30;
            default: return 5;
        }
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

    // 성장 완료 식물 저장 (예: CompletedPlants 테이블에 기록)
    private void saveCompletedPlant(String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("completedTime", System.currentTimeMillis());
        db.insert("CompletedPlants", null, values);

        Toast.makeText(context, "식물 '" + name + "'이(가) 성장 완료되었습니다!", Toast.LENGTH_SHORT).show();
        //상장완료된 식물은 collectionRoom에 저장


        //식물 성장 완료
        SoundManager.playSFX("garden_plant_grow_max");

        // 식물 10개가 완성됐는지 확인
        Cursor countCursor = db.rawQuery("SELECT COUNT(*) FROM CompletedPlants", null);
        if (countCursor.moveToFirst() && countCursor.getInt(0) >= 10) {
            checkAndShowEnding(); // 엔딩 진입 조건
        }
        countCursor.close();
    }
     //엔딩 확인 및 처리
    private void checkAndShowEnding() {
        Cursor cursor = db.rawQuery("SELECT name FROM CompletedPlants", null);
        int totalScore = 0;
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            totalScore += name.contains("초급") ? 10 : name.contains("중급") ? 20 : 30;
        }
        cursor.close();

        String ending;
        int endingID = 1;
        if (totalScore >= 270){
            ending = "전설의 정원사 엔딩";
            endingID = 3;
        }
        else if (totalScore >= 200){
            ending = "열정의 정원사 엔딩";
            endingID = 2;
        }
        else{
            ending = "평범한 정원사 엔딩";
            endingID = 1;
        }

        db.execSQL("INSERT INTO UnlockedEndings (ending) VALUES (?)", new Object[]{ending});

        //엔딩으로 이동
        SoundManager.playSFX("garden_game_clear");
        // 여기서 엔딩 Activity로 전환 가능
        Toast.makeText(context, "엔딩 진입: " + ending, Toast.LENGTH_LONG).show();
        //storyManager로 intent
        Intent endStory = new Intent(context, StoryManager.class);
        endStory.putExtra("storyType", endingID);
        context.startActivity(endStory);
    }

    public int getCurrentPlantStep() {
        return dbHelper.getCurrentPlantStep();
    }

    public int getCurrentPlantAchievement() {
        return dbHelper.getCurrentPlantAchievement();
    }

    public String getCurrentPlantGrade() {
        return dbHelper.getCurrentPlantGrade();
    }

    public int finalAchievementScore() {
        return 0;
    }
}

