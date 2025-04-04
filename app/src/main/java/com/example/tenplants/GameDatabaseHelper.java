package com.example.tenplants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabaseHelper  extends SQLiteOpenHelper {         //양새롬
    private static final String DATABASE_NAME = "IdleTycoon.db";
    private static final int DATABASE_VERSION = 1;

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {//테이블을 생성하는 SQL문장
        db.execSQL("CREATE TABLE PlayerData (id INTEGER PRIMARY KEY, energy INTEGER)");
        db.execSQL("CREATE TABLE CurrentPlants (id INTEGER PRIMARY KEY, name TEXT, grade INTEGER, growth INTEGER, maxGrowth INTEGER)");
        db.execSQL("CREATE TABLE UnlockedPlants (id INTEGER PRIMARY KEY, name TEXT)");
        db.execSQL("CREATE TABLE UnlockedEndings (id INTEGER PRIMARY KEY, ending TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//테이블을 업그레이드하는 SQL문장
        db.execSQL("DROP TABLE IF EXISTS PlayerData");
        db.execSQL("DROP TABLE IF EXISTS CurrentPlants");
        db.execSQL("DROP TABLE IF EXISTS UnlockedPlants");
        db.execSQL("DROP TABLE IF EXISTS UnlockedEndings");
        onCreate(db);
    }
    
}

