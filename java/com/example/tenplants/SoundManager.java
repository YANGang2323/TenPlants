package com.example.tenplants;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

public class SoundManager {

    // 현재 재생 중인 배경음악(MediaPlayer)
    private static MediaPlayer currentBGM;

    // BGM 2개 미리 등록
    private static MediaPlayer storyBGM;
    private static MediaPlayer gameBGM;

    // 효과음 재생용 SoundPool
    private static SoundPool soundPool;

    // 효과음 이름과 사운드 ID를 저장하는 맵
    private static HashMap<String, Integer> soundEffects = new HashMap<>();

    // 앱 전체 Context 저장용
    private static Context context;

    // UI 스레드에서 실행하기 위한 Handler (페이드 효과용)
    private static Handler handler = new Handler(Looper.getMainLooper());

    // 페이드 효과 관련 상수들
    private static final int FADE_DURATION = 1000;  // 페이드 지속 시간 (밀리초 단위)
    private static final int FADE_INTERVAL = 100;   // 페이드 중 볼륨 업데이트 간격 (밀리초)
    private static final float MAX_VOLUME = 1.0f;   // 최대 볼륨값

    // 현재 설정된 배경음악과 효과음 볼륨 (0.0f ~ 1.0f)
    private static float bgmVolume = MAX_VOLUME;
    private static float sfxVolume = MAX_VOLUME;

    /**
     * SoundManager 초기화 메서드.
     * Application 또는 첫 Activity에서 1회 호출해줍니다.
     * 여기서 BGM과 효과음을 미리 로드하고 준비합니다.
     */
    public static void init(Context ctx) {
        context = ctx.getApplicationContext();

        // 스토리 bgm, 반복 재생 설정, 볼륨 0으로 초기화(페이드 인용)
        storyBGM = MediaPlayer.create(context, R.raw.bgmstory);
        storyBGM.setLooping(true);
        storyBGM.setVolume(0f, 0f);

        // 타이틀 및 게임 bgm, 반복 재생 설정, 볼륨 0으로 초기화
        gameBGM = MediaPlayer.create(context, R.raw.bgmtitlegardencollection);
        gameBGM.setLooping(true);
        gameBGM.setVolume(0f, 0f);

        // 효과음 재생을 위한 SoundPool 생성 (최대 5개 동시 재생 가능)
        soundPool = new SoundPool.Builder().setMaxStreams(5).build();

        // 효과음 리소스 미리 로드 및 사운드 ID 저장

        //게임 시작 버튼 누를 때
        soundEffects.put("story_start", soundPool.load(context, R.raw.storystart, 1));

        //garden 식물 성장 행위
        soundEffects.put("garden_dust", soundPool.load(context, R.raw.gardendust, 1));
        soundEffects.put("garden_fertilizer", soundPool.load(context, R.raw.gardenfertilizer, 1));
        soundEffects.put("garden_sing", soundPool.load(context, R.raw.gardensing, 1));
        soundEffects.put("garden_hand", soundPool.load(context, R.raw.gardenhand, 1));
        soundEffects.put("garden_light_on", soundPool.load(context, R.raw.gardenlighton, 1));
        soundEffects.put("garden_water", soundPool.load(context, R.raw.gardenwater, 1));

        //씨앗 심기
        soundEffects.put("garden_seeding", soundPool.load(context, R.raw.gardenseeding, 1));

        //식물 성장 완료
        soundEffects.put("garden_plant_grow_max", soundPool.load(context, R.raw.gardenplantgrowmax, 1));

        //성취도? 식물 10개 수집 완료
        soundEffects.put("garden_game_clear", soundPool.load(context, R.raw.gardengameclear, 1));

        //스토리 엔딩 마지막 버튼 클릭했을 때
        soundEffects.put("story_end", soundPool.load(context, R.raw.andthisishowitends, 1));
    }

    /**
     * 배경음악 전환 메서드.
     * "menu" 또는 "game" 문자열을 전달받아 해당 BGM으로 페이드 전환 재생.
     * 동일한 BGM 요청시 무시.
     */
    public static void playBGM(String bgmKey) {
        MediaPlayer nextBGM = "menu".equals(bgmKey) ? storyBGM :
                "game".equals(bgmKey) ? gameBGM : null;

        if (nextBGM == null) return;           // 잘못된 키면 무시
        if (nextBGM == currentBGM) return;    // 이미 재생 중인 BGM이면 무시

        // 현재 BGM을 페이드 아웃하고 다음 BGM을 페이드 인
        fadeOutCurrentBGM(() -> {
            currentBGM = nextBGM;
            currentBGM.seekTo(0);  // 처음부터 재생
            currentBGM.start();
            fadeIn(currentBGM);
        });
    }

    /**
     * 현재 재생 중인 BGM을 페이드 아웃하는 메서드.
     * 볼륨을 점차 줄이다가 재생을 멈추고 onComplete 콜백 실행.
     */
    private static void fadeOutCurrentBGM(Runnable onComplete) {
        if (currentBGM == null) {
            onComplete.run();
            return;
        }

        final float[] volume = {bgmVolume}; // 배열로 참조 공유

        handler.post(new Runnable() {
            @Override
            public void run() {
                volume[0] -= bgmVolume * FADE_INTERVAL / FADE_DURATION;
                if (volume[0] <= 0f) {
                    // 볼륨 0 이하이면 중지 및 음소거 처리 후 완료 콜백 실행
                    currentBGM.pause();
                    currentBGM.setVolume(0f, 0f);
                    onComplete.run();
                } else {
                    // 볼륨 감소 계속
                    currentBGM.setVolume(volume[0], volume[0]);
                    handler.postDelayed(this, FADE_INTERVAL);
                }
            }
        });
    }

    /**
     * 새로 시작하는 BGM을 페이드 인하는 메서드.
     * 볼륨을 0부터 현재 설정된 볼륨까지 점차 증가시킴.
     */
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

    /**
     * 효과음 재생 메서드.
     * key에 맞는 효과음이 등록되어있으면 볼륨 설정에 맞게 재생.
     */
    public static void playSFX(String key) {
        Integer soundId = soundEffects.get(key);
        if (soundId != null) {
            soundPool.play(soundId, sfxVolume, sfxVolume, 1, 0, 1);
        }
    }

    /**
     * 배경음악 볼륨 조절 메서드.
     * 0.0f ~ 1.0f 범위의 실수 값으로 설정.
     * 현재 재생 중인 BGM에도 즉시 반영.
     */
    public static void setBGMVolume(float volume) {
        bgmVolume = volume;
        if (currentBGM != null) {
            currentBGM.setVolume(volume, volume);
        }
    }

    /**
     * 효과음 볼륨 조절 메서드.
     * 0.0f ~ 1.0f 범위.
     */
    public static void setSFXVolume(float volume) {
        sfxVolume = volume;
    }

    /**
     * 앱 종료 시 호출해 리소스 해제.
     * MediaPlayer와 SoundPool 자원 반환.
     */
    public static void release() {
        stopAllBGM();

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    /**
     * 등록된 모든 BGM 중지 및 해제.
     */
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

