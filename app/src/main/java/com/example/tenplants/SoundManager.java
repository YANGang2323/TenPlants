package com.example.tenplants;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

public class SoundManager {

    private static MediaPlayer currentBGM;
    private static MediaPlayer storyBGM;
    private static MediaPlayer gameBGM;

    private static SoundPool soundPool;
    private static HashMap<String, Integer> soundEffects = new HashMap<>();
    private static Context context;
    private static Handler handler = new Handler(Looper.getMainLooper());

    private static final int FADE_DURATION = 1000;
    private static final int FADE_INTERVAL = 100;
    private static final float MAX_VOLUME = 1.0f;

    private static float bgmVolume = MAX_VOLUME;
    private static float sfxVolume = MAX_VOLUME;

    public static void init(Context ctx) {
        context = ctx.getApplicationContext();

        // 저장된 사용자 설정 적용
        int savedBGM = SettingManager.getInt(context, SettingManager.KEY_BGM_VOLUME, 100);
        int savedSFX = SettingManager.getInt(context, SettingManager.KEY_SFX_VOLUME, 100);
        bgmVolume = savedBGM / 100f;
        sfxVolume = savedSFX / 100f;

        storyBGM = MediaPlayer.create(context, R.raw.bgmstory);
        storyBGM.setLooping(true);
        storyBGM.setVolume(0f, 0f);

        gameBGM = MediaPlayer.create(context, R.raw.bgmtitlegardencollection);
        gameBGM.setLooping(true);
        gameBGM.setVolume(0f, 0f);

        soundPool = new SoundPool.Builder().setMaxStreams(5).build();

        // 효과음 등록
        soundEffects.put("story_start", soundPool.load(context, R.raw.storystart, 1));
        soundEffects.put("alert", soundPool.load(context, R.raw.alert, 1));
        soundEffects.put("move_place", soundPool.load(context, R.raw.move_to_garden_collection, 1));
        soundEffects.put("garden_dust", soundPool.load(context, R.raw.gardendust, 1));
        soundEffects.put("garden_fertilizer", soundPool.load(context, R.raw.gardenfertilizer, 1));
        soundEffects.put("garden_sing", soundPool.load(context, R.raw.gardensing, 1));
        soundEffects.put("garden_hand", soundPool.load(context, R.raw.gardenhand, 1));
        soundEffects.put("garden_light_on", soundPool.load(context, R.raw.gardenlighton, 1));
        soundEffects.put("garden_water", soundPool.load(context, R.raw.gardenwater, 1));
        soundEffects.put("garden_seeding", soundPool.load(context, R.raw.gardenseeding, 1));
        soundEffects.put("garden_plant_grow_max", soundPool.load(context, R.raw.gardenplantgrowmax, 1));
        soundEffects.put("garden_game_clear", soundPool.load(context, R.raw.gardengameclear, 1));
        soundEffects.put("story_end", soundPool.load(context, R.raw.andthisishowitends, 1));
    }

    public static void playBGM(String bgmKey) {
        MediaPlayer nextBGM = "story".equals(bgmKey) ? storyBGM :
                "game".equals(bgmKey) ? gameBGM : null;

        if (nextBGM == null || nextBGM == currentBGM) return;

        fadeOutCurrentBGM(() -> {
            currentBGM = nextBGM;
            currentBGM.seekTo(0);
            currentBGM.start();
            fadeIn(currentBGM);
        });
    }

    private static void fadeOutCurrentBGM(Runnable onComplete) {
        if (currentBGM == null) {
            onComplete.run();
            return;
        }

        final float[] volume = {bgmVolume};

        handler.post(new Runnable() {
            @Override
            public void run() {
                volume[0] -= bgmVolume * FADE_INTERVAL / FADE_DURATION;
                if (volume[0] <= 0f) {
                    currentBGM.pause();
                    currentBGM.setVolume(0f, 0f);
                    onComplete.run();
                } else {
                    currentBGM.setVolume(volume[0], volume[0]);
                    handler.postDelayed(this, FADE_INTERVAL);
                }
            }
        });
    }

    private static void fadeIn(MediaPlayer player) {
        final float[] volume = {0f};

        handler.post(new Runnable() {
            @Override
            public void run() {
                volume[0] += bgmVolume * FADE_INTERVAL / FADE_DURATION;
                if (volume[0] >= bgmVolume) {
                    player.setVolume(bgmVolume, bgmVolume);
                } else {
                    player.setVolume(volume[0], volume[0]);
                    handler.postDelayed(this, FADE_INTERVAL);
                }
            }
        });
    }

    public static void playSFX(String key) {
        Integer soundId = soundEffects.get(key);
        if (soundId != null) {
            soundPool.play(soundId, sfxVolume, sfxVolume, 1, 0, 1);
        }
    }

    public static void setBGMVolume(float volume) {
        bgmVolume = volume;
        if (currentBGM != null) {
            currentBGM.setVolume(volume, volume);
        }

        // 저장 (0.0f~1.0f → 0~100)
        SettingManager.saveInt(context, SettingManager.KEY_BGM_VOLUME, (int)(volume * 100));
    }

    public static void setSFXVolume(float volume) {
        sfxVolume = volume;
        SettingManager.saveInt(context, SettingManager.KEY_SFX_VOLUME, (int)(volume * 100));
    }

    public static void pauseBGM() {
        if (currentBGM != null && currentBGM.isPlaying()) {
            currentBGM.pause();
        }
    }

    public static void resumeBGM() {
        if (currentBGM != null && !currentBGM.isPlaying()) {
            currentBGM.start();
        }
    }

    public static void release() {
        stopAllBGM();

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    private static void stopAllBGM() {
        if (storyBGM != null) {
            storyBGM.stop();
            storyBGM.release();
            storyBGM = null;
        }

        if (gameBGM != null) {
            gameBGM.stop();
            gameBGM.release();
            gameBGM = null;
        }

        currentBGM = null;
    }
}