package com.example.tenplant;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingManager {

    private static final String PREF_NAME = "app_settings";

    /**
     * 정수형 값 저장 (예: 볼륨 0~100)
     * @param context Context
     * @param key 저장할 키 (ex: "bgm_volume")
     * @param value 저장할 값
     */
    public static void saveInt(Context context, String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(key, value).apply();
    }

    /**
     * 정수형 값 불러오기 (기본값 지정 가능)
     * @param context Context
     * @param key 불러올 키
     * @param defaultValue 기본값
     * @return 저장된 값 또는 기본값
     */
    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(key, defaultValue);
    }
}
