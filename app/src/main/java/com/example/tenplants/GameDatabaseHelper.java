package com.example.tenplants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GameDatabaseHelper  extends SQLiteOpenHelper {         //양새롬

    private static final String DATABASE_NAME = "IdleTycoon.db";
    private static final int DATABASE_VERSION = 1;
    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//테이블을 생성하는 SQL문장
        db.execSQL("CREATE TABLE PlayerData (id INTEGER PRIMARY KEY, energy INTEGER, lastUpdateTime INTEGER, finalAchievementScore INTEGER)");
        db.execSQL("CREATE TABLE CurrentPlants (id INTEGER PRIMARY KEY, name TEXT, grade INTEGER, step INTEGER DEFAULT 0, growth INTEGER, maxGrowth INTEGER)");
        db.execSQL("CREATE TABLE CompletedPlants (id INTEGER PRIMARY KEY, name TEXT, grade INTEGER, completedTime INTEGER)");
        db.execSQL("CREATE TABLE UnlockedPlants (id INTEGER PRIMARY KEY, name TEXT)");
        db.execSQL("CREATE TABLE UnlockedEndings (id INTEGER PRIMARY KEY, ending TEXT)");
        db.execSQL("CREATE TABLE lockedPlants (id INTEGER PRIMARY KEY, name TEXT)");
        db.execSQL("CREATE TABLE lockedEndings (id INTEGER PRIMARY KEY, ending TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//테이블을 업그레이드하는 SQL문장
        db.execSQL("DROP TABLE IF EXISTS PlayerData");
        db.execSQL("DROP TABLE IF EXISTS CurrentPlants");
        db.execSQL("DROP TABLE IF EXISTS CompletedPlants");
        db.execSQL("DROP TABLE IF EXISTS UnlockedPlants");
        db.execSQL("DROP TABLE IF EXISTS UnlockedEndings");
        db.execSQL("DROP TABLE IF EXISTS lockedPlants");
        db.execSQL("DROP TABLE IF EXISTS lockedEndings");
        onCreate(db);
    }

    //데이터 삽입
    public long insertPlayerData(int energy, long lastUpdateTime, int finalAchievementScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("energy", energy);
        values.put("lastUpdateTime", lastUpdateTime);
        values.put("finalAchievementScore", finalAchievementScore);

        // PlayerData 테이블에 데이터를 삽입
        return db.insert("PlayerData", null, values);
    }
    public long insertCurrentPlant(String name, int grade,int step, int growth, int maxGrowth) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("grade", grade);  // 여기서 grade는 enum 값으로, 저장 시 정수로 변환해야 할 수도 있음
        values.put("step", step);
        values.put("growth", growth);
        values.put("maxGrowth", maxGrowth);

        // CurrentPlants 테이블에 데이터를 삽입
        return db.insert("CurrentPlants", null, values);
    }

    //set
    public void setPlayerEnergyToMax(int playerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 기존 energy 외의 다른 컬럼은 유지하면서 energy만 수정
        ContentValues values = new ContentValues();
        values.put("energy", 120);

        db.update("PlayerData", values, "id = ?", new String[]{String.valueOf(playerId)});

        db.close();
        Log.i("DB_UPDATE", "id가 " + playerId + "인 플레이어의 energy를 120으로 설정했습니다.");
    }
    //데이터 조회
    public int getPlayerScoreData() {
        SQLiteDatabase db = this.getReadableDatabase();
        // PlayerData 테이블에서 데이터를 조회
        Cursor cursor = db.rawQuery("SELECT * FROM PlayerData", null);
        int score = 0;
        if (cursor.moveToFirst()) {
            int scoreIndex = cursor.getColumnIndex("finalAchievementScore");
            if (scoreIndex != -1) {  // 컬럼 존재 확인
                score = cursor.getInt(scoreIndex);
            } else {
                // 컬럼이 없으면 로그 출력 등 처리
                Log.e("DB_ERROR", "finalAchievementScore 컬럼이 없습니다.");
            }
        }
        cursor.close();
        return score; //플레이어의 성취도점수반환
    }
//    public Cursor getPlayerData() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        // PlayerData 테이블에서 데이터를 조회
//        return db.rawQuery("SELECT * FROM PlayerData", null);
//    }
    public Cursor getCurrentPlants() {
        SQLiteDatabase db = this.getReadableDatabase();
        // CurrentPlants 테이블에서 데이터를 조회
        return db.rawQuery("SELECT * FROM CurrentPlants", null);
    }
    public Cursor getCompletedPlants() { //cursor
        SQLiteDatabase db = this.getReadableDatabase();
        // CompletedPlants 테이블에서 데이터를 조회
        return db.rawQuery("SELECT * FROM CompletedPlants", null);

    }
    public List<String> getCompletedPlantNames() {
        List<String> completedPlants = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM CompletedPlants", null);
        String completedPlantName = null;

        if (cursor.moveToFirst()) {
            do {
                int nameIndex = cursor.getColumnIndex("name");
                completedPlantName = cursor.getString(nameIndex);
                completedPlants.add(completedPlantName);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return completedPlants;
    }
    public int getCompletedPlantCount() { //개수
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM CompletedPlants", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count; // 완료된 식물 개수 반환
    }
    public int getMaxGrowth(String plantName) {
        switch (plantName) {
            case "Rose":
            case "ardisia_pusilla":
            case "ficus_pumila":
            case "sansevieria":
                return 100;  // BASIC 초급

            case "geranium_palustre":
            case "kerria_japonica":
            case "trigonotis_peduncularis":
            case "eglantine":
            case "narcissus":
                return 240;  // INTERMEDIATE 중급

            case "coreopsis_basalis":
            case "lavandula_angustifolia":
            case "pansy":
            case "rhododendron_schlippenbachii":
                return 300;  // ADVANCED 고급

            default:
                Log.i("씨앗", "해당없음");
                return 0;  // 알 수 없는 식물
        }
    }

    public String getCurrentPlantName() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM CurrentPlants", null);
        String plantName = null;

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            plantName = cursor.getString(nameIndex);
        }
        cursor.close();
        return plantName; // 현재 키우고 있는 식물 이름 반환
    }

    public int getCurrentPlantStep() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT step FROM CurrentPlants", null);
        int step = 0;

        if (cursor.moveToFirst()) {
            int stepIndex = cursor.getColumnIndex("step");
            step = cursor.getInt(stepIndex);
        }
        cursor.close();
        return step;
    }

    public int getFinalAchievementScore() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT finalAchievementScore FROM PlayerData", null);
        int finalAchievementScore = 0;

        if (cursor.moveToFirst()) {
            int finalAchievementScoreIndex = cursor.getColumnIndex("finalAchievementScore");
            finalAchievementScore = cursor.getInt(finalAchievementScoreIndex);
        }
        cursor.close();
        return finalAchievementScore;
    }
//    public int getCurrentPlantAchievement() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT grade FROM CurrentPlants", null);
//        int achievement = 0;
//
//        if (cursor.moveToFirst()) {
//            int gradeIndex = cursor.getColumnIndex("grade");
//            int grade = cursor.getInt(gradeIndex);
//            if (grade == 0) achievement = 10; // BASIC
//            else if (grade == 1) achievement = 20; // INTERMEDIATE
//            else if (grade == 2) achievement = 30; // ADVANCED
//        }
//        cursor.close();
//        return achievement;
//    }

    public String getCurrentPlantGrade() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT grade FROM CurrentPlants", null);
        String grade = "알 수 없음";

        if (cursor.moveToFirst()) {
            int gradeIndex = cursor.getColumnIndex("grade");
            int gradeValue = cursor.getInt(gradeIndex);
            if (gradeValue == 0) grade = "초급";
            else if (gradeValue == 1) grade = "중급";
            else if (gradeValue == 2) grade = "고급";
        }
        cursor.close();
        return grade;
    }
    public Integer getCurrentPlantGradeInt() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT grade FROM CurrentPlants", null);
        int grade = 0;

        if (cursor.moveToFirst()) {
            int gradeIndex = cursor.getColumnIndex("grade");
            grade = cursor.getInt(gradeIndex);
        }
        cursor.close();
        return grade;
    }
//    public int getCurrentPlantGrowth() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT growth FROM CurrentPlants LIMIT 1", null);
//        int growth = 0;
//
//        if (cursor.moveToFirst()) {
//            int growthIndex = cursor.getColumnIndex("growth");
//            growth = cursor.getInt(growthIndex);
//        }
//        cursor.close();
//        return growth;
//    }
//
//    //데이터 업데이트
//    public int updatePlantGrowth(int plantId, int newGrowth) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("growth", newGrowth);
//
//        // 특정 plantId를 가진 식물의 growth를 업데이트
//        return db.update("CurrentPlants", values, "id = ?", new String[]{String.valueOf(plantId)});
//    }
//
//    //데이터 삭제
//    public void deletePlant(int plantId) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        // 특정 plantId를 가진 식물을 삭제
//        db.delete("CurrentPlants", "id = ?", new String[]{String.valueOf(plantId)});
//    }


    public int calculatePlantStep(int growth, int grade) {
        int step = 0;

        if (grade == 0) { // BASIC (초급)
            if (growth >= 100) step = 3;
            else if (growth >= 40) step = 2;
            else if (growth >= 20) step = 1;
        } else if (grade == 1) { // INTERMEDIATE (중급)
            if (growth >= 240) step = 3;
            else if (growth >= 100) step = 2;
            else if (growth >= 50) step = 1;
        } else if (grade == 2) { // ADVANCED (고급)
            if (growth >= 300) step = 3;
            else if (growth >= 200) step = 2;
            else if (growth >= 100) step = 1;
        }
        return step;
    }
    public int accumulateAchievement(int grade) {
        int achievementPoints = 0;

        // 등급별 성취도 점수 부여
        switch (grade) {
            case 0: // BASIC
                achievementPoints = 10;
                break;
            case 1: // INTERMEDIATE
                achievementPoints = 20;
                break;
            case 2: // ADVANCED
                achievementPoints = 30;
                break;
            default:
                Log.w("accumulateAchievement", "알 수 없는 등급: " + grade);
                return getFinalAchievementScore(); // 오류 발생 시 기존 점수 반환
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT finalAchievementScore FROM PlayerData", null);

        int currentScore = 0;
        int updatedScore = 0;

        if (cursor.moveToFirst()) {
            int currentScoreIndex = cursor.getColumnIndex("finalAchievementScore");
            currentScore = cursor.getInt(currentScoreIndex);
            updatedScore = currentScore + achievementPoints;

            ContentValues values = new ContentValues();
            values.put("finalAchievementScore", updatedScore);
            db.update("PlayerData", values, null, null);
        }

        cursor.close();

        return updatedScore; // 누적된 전체 성취도 점수를 반환
    }
}

