package com.example.tenplants;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.SeekBar;

public class SettingManager {

    private static final String PREF_NAME = "app_settings";

    public static final String KEY_BGM_VOLUME = "bgm_volume"; // 0~100
    public static final String KEY_SFX_VOLUME = "sfx_volume"; // 0~100

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(key, value).apply();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(key, defaultValue);
    }

    /**
     * 볼륨 조절 SeekBar 2개(BGMbar, SFbar)를 초기화하고 설정 연동.
     * @param context context
     * @param BGMbar 배경음악 조절 SeekBar
     * @param SFbar 효과음 조절 SeekBar
     */
    public static void initVolumeSeekBars(Context context, SeekBar BGMbar, SeekBar SFbar) {
        // 초기 값 설정
        int savedBGM = getInt(context, KEY_BGM_VOLUME, 100);
        int savedSFX = getInt(context, KEY_SFX_VOLUME, 100);
        BGMbar.setProgress(savedBGM);
        SFbar.setProgress(savedSFX);

        // BGM SeekBar 변경 리스너
        BGMbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                saveInt(context, KEY_BGM_VOLUME, progress);
                SoundManager.setBGMVolume(progress / 100f);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // SFX SeekBar 변경 리스너
        SFbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                saveInt(context, KEY_SFX_VOLUME, progress);
                SoundManager.setSFXVolume(progress / 100f);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
}