package com.allvoicetranslator.language.translator.ads.FirebaseADHandlers;

import static com.allvoicetranslator.language.translator.ads.Utils.Constants.isNull;

import android.app.Application;
import android.util.Log;

import com.allvoicetranslator.language.translator.R;
import com.allvoicetranslator.language.translator.ads.PreferencesManager.AppPreferences;
import com.allvoicetranslator.language.translator.ads.Utils.Constants;
import com.allvoicetranslator.language.translator.ads.Utils.Global;
import com.allvoicetranslator.language.translator.utils.SharePreferences;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.MobileAds;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.FileInputStream;

public class MyApplication extends Application {

    private static MyApplication instance;
    private static SharePreferences preferences;
    private static Translate translate;
    private static RequestQueue requestQueue;

    public static RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getInstance());
        }
        return requestQueue;
    }

    public static Translate getTranslate() {
        String API_KEY = getPreferences().getAPIKEY();
        if (isNull(API_KEY)) {
            getAPIKEY();
            API_KEY = getPreferences().getAPIKEY();
        }
        if (translate == null) {
            try {
                Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(API_KEY));
                translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();
            } catch (Exception e) {
                translate = TranslateOptions.newBuilder().setApiKey(API_KEY).build().getService();
            }
        }
        return translate;
    }

    public static SharePreferences getPreferences() {
        if (preferences == null) preferences = new SharePreferences(getInstance());
        return preferences;
    }


    public static synchronized MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppPreferences appPreferencesManger = new AppPreferences(this);
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.ADSJSON);
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        Constants.adsJsonPOJO = Global.getAdsData(appPreferencesManger.getAdsModel());

        if (Constants.adsJsonPOJO != null && !isNull(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getValue())) {
            Constants.adsJsonPOJO = Global.getAdsData(appPreferencesManger.getAdsModel());
            Constants.hitCounter = Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits());
        } else {
            FirebaseUtils.initiateAndStoreFirebaseRemoteConfig(this, adsJsonPOJO -> {
                appPreferencesManger.setAdsModel(adsJsonPOJO);
                Constants.adsJsonPOJO = adsJsonPOJO;
                Constants.hitCounter = Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits());
            });
        }

        MobileAds.initialize(this, initializationStatus -> {});
        new AppOpenAds(this);

        int val = getPreferences().getUpdate();
        if (val == 0 || val == 10 || val == 24) getAPIKEY();


    }

    private static void getAPIKEY() {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(1).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String key = mFirebaseRemoteConfig.getAll().get(getInstance().getString(R.string.firebase_key_name)).asString();
                getPreferences().setAPIKEY(key);
            }
        }).addOnFailureListener(e -> Log.e("ad failed", e.getLocalizedMessage()));
    }

}