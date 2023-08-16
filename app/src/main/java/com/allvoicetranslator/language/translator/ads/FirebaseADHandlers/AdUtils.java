package com.allvoicetranslator.language.translator.ads.FirebaseADHandlers;

import static com.allvoicetranslator.language.translator.ads.Utils.Constants.isNull;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.allvoicetranslator.language.translator.R;
import com.allvoicetranslator.language.translator.ads.Interfaces.AppInterfaces;
import com.allvoicetranslator.language.translator.ads.PreferencesManager.AppPreferences;
import com.allvoicetranslator.language.translator.ads.Utils.ConnectionDetector;
import com.allvoicetranslator.language.translator.ads.Utils.Constants;
import com.allvoicetranslator.language.translator.ads.Utils.Global;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.gson.Gson;

public class AdUtils {

    private static ConnectionDetector cd;


    public static void showRewardAd(Activity activity) {
        AppPreferences appPreferencesManger = new AppPreferences(activity);
        Constants.adsJsonPOJO = new Gson().fromJson(appPreferencesManger.getAdsModel(), AdsJsonPOJO.class);
        cd = new ConnectionDetector(activity);

        if (cd.isConnectingToInternet()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(activity, Constants.adsJsonPOJO.getParameters().getRewarded_ad().getValueType(), adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Global.sout("RewardedAdLoadCallback: ", loadAdError.toString());
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    rewardedAd.show(activity, new OnUserEarnedRewardListener() {

                        @Override
                        public void onUserEarnedReward(RewardItem rewardItem) {
                            //TODO user earned reward --Not to implement now--
                        }
                    });
                    rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                        @Override
                        public void onAdShowedFullScreenContent() {
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            Global.sout("onAdFailedToShowFullScreenContent: ", adError.toString());
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Global.sout("onAdFailedToShowFullScreenContent: ", "onAdDismissedFullScreenContent");
                        }
                    });
                }

            });
        }
    }


    public static void loadInitialNativeList(Activity activity) {
        int i = 0;
        while (i < 10) {
            AppPreferences appPreferencesManger = new AppPreferences(activity);
            Constants.adsJsonPOJO = new Gson().fromJson(appPreferencesManger.getAdsModel(), AdsJsonPOJO.class);
            cd = new ConnectionDetector(activity);
            if (Constants.adsJsonPOJO != null && cd.isConnectingToInternet() && Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().getValue().equals("true")) {
                if (Constants.NativeAdsList != null && Constants.NativeAdsList.size() < 10) {
                    try {
                        AdLoader adLoader = new AdLoader.Builder(activity, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue()).forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(NativeAd nativeAd) {
                                Constants.NativeAdsList.add(nativeAd);
                            }
                        }).withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError adError) {

                            }
                        }).withNativeAdOptions(new NativeAdOptions.Builder().build()).build();
                        adLoader.loadAd(new AdRequest.Builder().build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            i++;
        }

    }


    public static void showNativeAd(Activity activity, LinearLayout adContainer, boolean isFullScreenAd) {
        AppPreferences appPreferencesManger = new AppPreferences(activity);
        Constants.adsJsonPOJO = new Gson().fromJson(appPreferencesManger.getAdsModel(), AdsJsonPOJO.class);
        cd = new ConnectionDetector(activity);
        if (cd.isConnectingToInternet() && Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().getValue().equals("true") && Constants.NativeAdsList.size() > 0) {
            NativeAd nativeAd = Constants.NativeAdsList.get(0);
            NativeAdView unifiedNativeAdView;
            try {
                if (isFullScreenAd) {
                    unifiedNativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.full_native_ad_param, null);
                } else {
                    unifiedNativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.small_native_ad, null);
                }
                unifiedNativeAdView.isHardwareAccelerated();
                populateUnifiedNativeAdView(nativeAd, unifiedNativeAdView, isFullScreenAd);
                Global.sout("ADs status", "native success");
                adContainer.removeAllViews();
                adContainer.addView(unifiedNativeAdView);
                adContainer.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                System.out.println("Native AD exception" + e.getMessage());
            }
        } else {
            adContainer.setVisibility(View.GONE);
        }


    }

    public static void showBannerAd(LinearLayout adContainer, Activity activity) {
        cd = new ConnectionDetector(activity);
        AppPreferences appPreferencesManger = new AppPreferences(activity);
        Constants.adsJsonPOJO = new Gson().fromJson(appPreferencesManger.getAdsModel(), AdsJsonPOJO.class);

        if (!isNull(Constants.adsJsonPOJO.getParameters().getBanner_id().getDefaultValue().getValue())) {
            if (cd.isConnectingToInternet() && Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().getValue().equals("true")) {
                AdView mAdView = new AdView(activity);
                if (cd.isConnectingToInternet()) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    try {
                        mAdView.setVisibility(View.VISIBLE);
                        mAdView.setEnabled(true);
                        mAdView.setAdSize(AdSize.BANNER);
                        mAdView.setAdUnitId(Constants.adsJsonPOJO.getParameters().getBanner_id().getDefaultValue().getValue());
                        adContainer.setVisibility(View.VISIBLE);

                        mAdView.setAdListener(new AdListener() {
                            public void onAdFailedToLoad(int i) {
                                adContainer.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                                //                        sAppOpenadsFLAG = false;
                            }

                            public void onAdLoaded() {
                            }

                            public void onAdOpened() {
                            }

                            public void onAdLeftApplication() {
                            }

                            public void onAdClosed() {
                            }
                        });

                        mAdView.loadAd(adRequest);
                        adContainer.removeAllViews();
                        adContainer.setPadding(5, 5, 5, 5);
                        adContainer.addView(mAdView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                mAdView.setVisibility(View.GONE);
                adContainer.setVisibility(View.GONE);
                adContainer.removeAllViews();
            }
        } else {
            adContainer.setVisibility(View.GONE);
            adContainer.removeAllViews();
        }
    }

    public static void showInterstitialAd(Activity activity, AppInterfaces.InterstitialADInterface interstitialADInterface) {
        AppPreferences appPreferencesManger = new AppPreferences(activity);
        Constants.adsJsonPOJO = new Gson().fromJson(appPreferencesManger.getAdsModel(), AdsJsonPOJO.class);
        cd = new ConnectionDetector(activity);
        if (cd.isConnectingToInternet() && Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().getValue().equals("true")) {
            if (Constants.hitCounter == Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits())) {
                Constants.hitCounter = 0;
                loadInterstitialAd(activity, interstitialADInterface);
            } else {
                Constants.hitCounter++;
                interstitialADInterface.adLoadState(false);
            }
        } else {
            interstitialADInterface.adLoadState(false);
        }
    }

    public static void loadAppOpenAds(Context context) {
        try {
            AppPreferences manager = new AppPreferences(context);
            Constants.adsJsonPOJO = Global.getAdsData(manager.getAdsModel());
            int i = 0;
            while (i < 3) {
                Global.sout("App open ads list size before >>>>", Constants.AppOpenAdsList.size());
                if (Constants.AppOpenAdsList.size() <= 4 && Constants.adsJsonPOJO != null) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    AppOpenAd.load(context, Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getValue(), adRequest, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, new AppOpenAd.AppOpenAdLoadCallback() {

                        @Override
                        public void onAdLoaded(@NonNull AppOpenAd ad) {
                            Constants.AppOpenAdsList.add(ad);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                        }

                    });
                } else {
                    break;
                }
                i++;
            }
        } catch (Exception e) {
            Global.sout("AppOpenAds list exception", e.getLocalizedMessage());
        }
    }

    public static void showAdIfAvailable(Activity activity, AppInterfaces.AppOpenADInterface appOpenADInterface) {
        AppPreferences appPreferencesManger = new AppPreferences(activity);
        Constants.adsJsonPOJO = new Gson().fromJson(appPreferencesManger.getAdsModel(), AdsJsonPOJO.class);
        if (Constants.AppOpenAdsList.size() > 0 && Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().getValue().equals("true")) {

            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    Constants.AppOpenAdsList.remove(0);
                    appOpenADInterface.appOpenAdState(true);
                    loadAppOpenAds(activity);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    Constants.AppOpenAdsList.remove(0);
                    appOpenADInterface.appOpenAdState(false);
                    loadAppOpenAds(activity);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    /*appOpenADInterface.appOpenAdState(true);*/
                }
            };

            Constants.AppOpenAdsList.get(0).setFullScreenContentCallback(fullScreenContentCallback);
            Constants.AppOpenAdsList.get(0).show(activity);
        } else if (Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().getValue().equals("true")) {
            AdRequest adRequest = new AdRequest.Builder().build();
            AppOpenAd.load(activity, Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getValue(), adRequest, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, new AppOpenAd.AppOpenAdLoadCallback() {

                @Override
                public void onAdLoaded(@NonNull AppOpenAd ad) {
                    Constants.AppOpenAdsList.add(ad);
                    showAdIfAvailable(activity, appOpenADInterface);
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error.
                    appOpenADInterface.appOpenAdState(false);
                }

            });
        } else {
            appOpenADInterface.appOpenAdState(false);
        }
    }

    private static void loadInterstitialAd(Activity activity, AppInterfaces.InterstitialADInterface interstitialADInterface) {
        if (Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().getValue().equals("true")) {
            if (!Global.isArrayListNull(Constants.InterstitialList)) {

                Constants.InterstitialList.get(0).setFullScreenContentCallback(new FullScreenContentCallback() {

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        interstitialADInterface.adLoadState(true);
                        Constants.InterstitialList.remove(0);
                        loadInitialInterstitialAds(activity);

                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        interstitialADInterface.adLoadState(false);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                    }
                });
                Constants.InterstitialList.get(0).show(activity);
            } else {
                loadInitialInterstitialAds(activity);
            }
        } else {
            interstitialADInterface.adLoadState(false);
        }
    }

    private static void populateUnifiedNativeAdView(NativeAd unifiedNativeAd, NativeAdView unifiedNativeAdView, boolean flag) {
        if (flag) {
            unifiedNativeAdView.setMediaView((MediaView) unifiedNativeAdView.findViewById(R.id.ad_media));
        }
        unifiedNativeAdView.setHeadlineView(unifiedNativeAdView.findViewById(R.id.ad_headline));
        unifiedNativeAdView.setBodyView(unifiedNativeAdView.findViewById(R.id.ad_body));
        unifiedNativeAdView.setCallToActionView(unifiedNativeAdView.findViewById(R.id.ad_call_to_action));
        unifiedNativeAdView.setIconView(unifiedNativeAdView.findViewById(R.id.ad_app_icon));
        unifiedNativeAdView.setPriceView(unifiedNativeAdView.findViewById(R.id.ad_price));
        unifiedNativeAdView.setStarRatingView(unifiedNativeAdView.findViewById(R.id.ad_stars));
        unifiedNativeAdView.setStoreView(unifiedNativeAdView.findViewById(R.id.ad_store));
        unifiedNativeAdView.setAdvertiserView(unifiedNativeAdView.findViewById(R.id.ad_advertiser));
        ((TextView) unifiedNativeAdView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());
        if (unifiedNativeAd.getBody() == null) {
            unifiedNativeAdView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            unifiedNativeAdView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) unifiedNativeAdView.getBodyView()).setText(unifiedNativeAd.getBody());
        }
        if (unifiedNativeAd.getCallToAction() == null) {
            unifiedNativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            unifiedNativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) unifiedNativeAdView.getCallToActionView()).setText(unifiedNativeAd.getCallToAction());
        }
        if (unifiedNativeAd.getIcon() == null) {
            unifiedNativeAdView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) unifiedNativeAdView.getIconView()).setImageDrawable(unifiedNativeAd.getIcon().getDrawable());
            unifiedNativeAdView.getIconView().setVisibility(View.VISIBLE);
        }
        if (unifiedNativeAd.getPrice() == null) {
            unifiedNativeAdView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            unifiedNativeAdView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) unifiedNativeAdView.getPriceView()).setText(unifiedNativeAd.getPrice());
        }
        if (unifiedNativeAd.getStore() == null) {
            unifiedNativeAdView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            unifiedNativeAdView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) unifiedNativeAdView.getStoreView()).setText(unifiedNativeAd.getStore());
        }
        if (unifiedNativeAd.getStarRating() == null) {
            unifiedNativeAdView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) unifiedNativeAdView.getStarRatingView()).setRating(unifiedNativeAd.getStarRating().floatValue());
            unifiedNativeAdView.getStarRatingView().setVisibility(View.VISIBLE);
        }
        if (unifiedNativeAd.getAdvertiser() == null) {
            unifiedNativeAdView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) unifiedNativeAdView.getAdvertiserView()).setText(unifiedNativeAd.getAdvertiser());
            unifiedNativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        unifiedNativeAdView.setNativeAd(unifiedNativeAd);
        Constants.NativeAdsList.remove(0);
    }

    public static void loadInitialInterstitialAds(Context context) {
        try {
            AppPreferences manager = new AppPreferences(context);
            Constants.adsJsonPOJO = Global.getAdsData(manager.getAdsModel());

            int i = 0;
            while (i < 3) {
                Global.sout("Interstitial ads list size before >>>>", Constants.InterstitialList.size());
                if (Constants.InterstitialList.size() <= 4 && Constants.adsJsonPOJO != null) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    InterstitialAd.load(context, Constants.adsJsonPOJO.getParameters().getFull_id().getDefaultValue().getValue(), adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            Constants.InterstitialList.add(interstitialAd);
                            Global.sout("Interstitial ads list size after >>>>", Constants.InterstitialList.size());
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        }
                    });
                } else {
                    break;
                }
                i++;
            }
        } catch (Exception e) {
            Global.sout("interstitial list exception", e.getLocalizedMessage());
        }

    }

    public static void loadCollapsibleAdsList(Activity activity) {
        AppPreferences manager = new AppPreferences(activity);
        Constants.adsJsonPOJO = Global.getAdsData(manager.getAdsModel());
        ConnectionDetector cd = new ConnectionDetector(activity);
        if (Constants.adsJsonPOJO != null && Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().getValue().equals("true") && cd.isConnectingToInternet()) {
            if (Constants.NativeAdsList.size() < 4) {
                int i = 0;
                while (i < 3) {
                    AdView adView = new AdView(activity);
                    AdSize customAdSize2 = new AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT);
                    adView.setAdSize(customAdSize2);
                    adView.setAdUnitId(Constants.adsJsonPOJO.getParameters().getCollapsibleAd().getDefaultValue().getValue());
                    Bundle extras = new Bundle();
                    extras.putString("collapsible", "top");
                    AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
                    adView.loadAd(adRequest);
                    Constants.CollapsibleAdsList.add(adView);
                    i++;
                }
            }
        }
    }

    public static void showCollapsibleAd(Activity activity, LinearLayout adContainer, AppInterfaces.CollapsibleAdInterface collapsibleAdInterface) {
        try {
            AppPreferences manager = new AppPreferences(activity);
            Constants.adsJsonPOJO = Global.getAdsData(manager.getAdsModel());
            AdView adView = new AdView(activity);
            AdSize customAdSize2 = new AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT);
            adView.setAdSize(customAdSize2);
            adView.setAdUnitId(Constants.adsJsonPOJO.getParameters().getCollapsibleAd().getDefaultValue().getValue());
            Bundle extras = new Bundle();
            extras.putString("collapsible", "top");
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    collapsibleAdInterface.setAdState(false);
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    adContainer.setGravity(Gravity.TOP);
                    collapsibleAdInterface.setAdState(true);
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    adContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    adContainer.setGravity(Gravity.BOTTOM);
                    collapsibleAdInterface.setAdState(false);
                }
            });

            adView.loadAd(adRequest);
            adContainer.addView(adView);

            adContainer.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            System.out.println("Collapsible exception>>>>>>>>>>> " + e.getMessage());
        }

       /* if (Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().getValue().equals("true") && Constants.CollapsibleAdsList.size() > 0) {
            Constants.CollapsibleAdsList.get(0).setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    Constants.CollapsibleAdsList.remove(0);
                    collapsibleAdInterface.setAdState(false);
                    loadCollapsibleAdsList(activity);
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Constants.CollapsibleAdsList.remove(0);
                    collapsibleAdInterface.setAdState(true);
                    loadCollapsibleAdsList(activity);

                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    adContainer.setVisibility(View.GONE);
                    Log.e( "","onAdFailedToLoad: Collpasible AD failed" );
                }
            });
            adContainer.addView(Constants.CollapsibleAdsList.get(0));
            adContainer.setVisibility(View.VISIBLE);
        }else {
            adContainer.setVisibility(View.GONE);
        }
    }*/
    }

}