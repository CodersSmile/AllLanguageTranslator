package com.allvoicetranslator.language.translator.ads.Interfaces;

import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdsJsonPOJO;

public class AppInterfaces {
    public interface AdDataInterface {
        void getAdData(AdsJsonPOJO adsJsonPOJO);
    }

    public interface InterstitialADInterface {
        void adLoadState(boolean isLoaded);
    }

    public interface AppOpenADInterface {
        void appOpenAdState(boolean state_load);
    }

    public interface AdapterClickInterface {
        void onClick(int position);
    }

    public interface CollapsibleAdInterface {
        void setAdState(boolean adState); //true=ad_open, false=ad_closed
    }

    public interface ShayriAdapterClick {
        void copy(int position);

        void share(int position);
    }
}
