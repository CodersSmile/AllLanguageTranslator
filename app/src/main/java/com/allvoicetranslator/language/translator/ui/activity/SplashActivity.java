package com.allvoicetranslator.language.translator.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.allvoicetranslator.language.translator.R;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdUtils;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.FirebaseUtils;
import com.allvoicetranslator.language.translator.ads.PreferencesManager.AppPreferences;
import com.allvoicetranslator.language.translator.ads.Utils.Constants;
import com.allvoicetranslator.language.translator.ads.Utils.Global;
import com.allvoicetranslator.language.translator.databinding.ActivitySplashBinding;
import com.google.firebase.messaging.FirebaseMessaging;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        AppPreferences appPreferences = new AppPreferences(this);
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.ADSJSON);
        Constants.adsJsonPOJO = Global.getAdsData(appPreferences.getAdsModel());

        if (Constants.adsJsonPOJO != null && !Constants.isNull(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getValue())) {
            Constants.adsJsonPOJO = Global.getAdsData(appPreferences.getAdsModel());
            Constants.hitCounter = Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits());
//            Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().setValue("false");
            openActivity();
        } else {
            FirebaseUtils.initiateAndStoreFirebaseRemoteConfig(this, adsJsonPOJO -> {
                appPreferences.setAdsModel(adsJsonPOJO);
                Constants.adsJsonPOJO = adsJsonPOJO;
                Constants.hitCounter = Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits());
//                Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().setValue("false");
                openActivity();
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        AdUtils.loadInitialInterstitialAds(this);
        AdUtils.loadAppOpenAds(this);
        AdUtils.loadInitialNativeList(this);

    }

    void openActivity() {
        AdUtils.showAdIfAvailable(this, state_load -> {
            new Handler().postDelayed(() -> {
                if (MyApplication.getPreferences().isFirstRun()){
                startActivity(new Intent(getApplicationContext(), OnBoardingActivity.class));
                finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

            }, 1600);
        });

    }

}