package com.allvoicetranslator.language.translator.ads.FirebaseADHandlers;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.allvoicetranslator.language.translator.ui.activity.SplashActivity;

public class AppOpenAds implements LifecycleObserver, Application.ActivityLifecycleCallbacks {

    MyApplication application;
    private Activity currentActivity;
    boolean isAdShowing = true;

    public AppOpenAds(MyApplication application) {
        this.application = application;
        application.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {

        if (!isAdShowing && currentActivity != null && (!currentActivity.getClass().getName().equals(SplashActivity.class.getName()))) {
            isAdShowing = true;
            AdUtils.showAdIfAvailable(currentActivity, state_load -> {
                isAdShowing = false;
            });
        } else {
            isAdShowing = false;
        }
    }
    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
        AdUtils.loadInitialInterstitialAds(activity);
        AdUtils.loadAppOpenAds(activity);
        AdUtils.loadInitialNativeList(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }


}
