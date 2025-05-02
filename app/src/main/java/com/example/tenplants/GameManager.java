package com.example.tenplants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GameManager {
    private GameDatabaseHelper dbHelper;

    public GameManager(Context context) {
        dbHelper = new GameDatabaseHelper(context);
    }

    // 기력 저장
    public void saveEnergy(int energy) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("energy", energy);
        db.insertWithOnConflict("PlayerData", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // 기력 불러오기
    public int getEnergy() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT energy FROM PlayerData", null);
        int energy = 0;
        if (cursor.moveToFirst()) {
            energy = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return energy;
    }







}

