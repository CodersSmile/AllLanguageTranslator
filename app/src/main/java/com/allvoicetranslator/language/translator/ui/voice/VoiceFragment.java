package com.allvoicetranslator.language.translator.ui.voice;

import static com.allvoicetranslator.language.translator.utils.Const.INPUT;
import static com.allvoicetranslator.language.translator.utils.Const.IN_SELECTED_LANGUAGE;
import static com.allvoicetranslator.language.translator.utils.Const.OUTPUT;
import static com.allvoicetranslator.language.translator.utils.Const.OUT_SELECTED_LANGUAGE;
import static com.allvoicetranslator.language.translator.utils.Const.SELECTED_LANGUAGE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.allvoicetranslator.language.translator.R;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdUtils;
import com.allvoicetranslator.language.translator.databinding.FragmentVoiceBinding;
import com.allvoicetranslator.language.translator.ui.activity.DashboardActivity;
import com.allvoicetranslator.language.translator.ui.fragment.LanguageFragment;

public class VoiceFragment extends Fragment {

    FragmentVoiceBinding binding;
    DashboardActivity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voice, container, false);

        activity = (DashboardActivity) requireActivity();

        AdUtils.showNativeAd(activity,  binding.adsView, true);
//        AdUtils.showNativeAd(activity, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), binding.adsView0, false, activity.getDrawable(R.drawable.img_voice_ads0));

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();


        AdUtils.loadInitialInterstitialAds(activity);
        AdUtils.loadAppOpenAds(activity);

        AdUtils.loadInitialNativeList(activity);

        binding.btnInput.setText(IN_SELECTED_LANGUAGE.getLanguageName());
        binding.btnOutput.setText(OUT_SELECTED_LANGUAGE.getLanguageName());

        binding.btnSwap.setOnClickListener(view -> {
            SELECTED_LANGUAGE = IN_SELECTED_LANGUAGE;
            IN_SELECTED_LANGUAGE = OUT_SELECTED_LANGUAGE;
            OUT_SELECTED_LANGUAGE = SELECTED_LANGUAGE;

            binding.btnInput.setText(IN_SELECTED_LANGUAGE.getLanguageName());
            binding.btnOutput.setText(OUT_SELECTED_LANGUAGE.getLanguageName());
            SELECTED_LANGUAGE = null;
        });

        binding.btnBack.setOnClickListener(view -> activity.openFirstFragment());

        activity.getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AdUtils.showInterstitialAd(activity, state_load -> {
                    activity.finish();
                });
//                activity.openFirstFragment();
            }
        });


        binding.btnSettings.setOnClickListener(view -> {

            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.drawerOpen();
            });


        });

        binding.btnInput.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.openFragment(new LanguageFragment(new VoiceFragment(), INPUT));
            });
        });

        binding.btnOutput.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.openFragment(new LanguageFragment(new VoiceFragment(), OUTPUT));
            });

        });

        binding.btnStart.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.openFragment(new RecordVoiceFragment(new VoiceFragment(), IN_SELECTED_LANGUAGE, OUT_SELECTED_LANGUAGE));
            });
        });

    }
}