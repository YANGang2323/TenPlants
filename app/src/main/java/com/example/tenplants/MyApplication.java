package com.example.tenplants;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private int startedActivityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        startedActivityCount++;
        if (startedActivityCount == 1) {
            // 앱이 포그라운드로 전환됨
            SoundManager.resumeBGM();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        startedActivityCount--;
        if (startedActivityCount == 0) {
            // 앱이 백그라운드로 전환됨 (overview 포함)
            SoundManager.pauseBGM();
        }
    }

    // 다른 콜백은 생략 가능
    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
    @Override public void onActivityResumed(Activity activity) {}
    @Override public void onActivityPaused(Activity activity) {}
    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
    @Override public void onActivityDestroyed(Activity activity) {}
}
