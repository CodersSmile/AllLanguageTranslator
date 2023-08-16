package com.allvoicetranslator.language.translator.ui.voice;

import static com.allvoicetranslator.language.translator.utils.Const.TEXTEXTRACT;
import static com.allvoicetranslator.language.translator.utils.Utility.copyToClipboard;
import static com.allvoicetranslator.language.translator.utils.Utility.getSingleTranslate;

import android.content.Intent;
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
import com.allvoicetranslator.language.translator.databinding.FragmentTranslateResultBinding;
import com.allvoicetranslator.language.translator.models.LanguagesModel;
import com.allvoicetranslator.language.translator.ui.activity.DashboardActivity;
import com.allvoicetranslator.language.translator.utils.TTS;

public class TranslateResultFragment extends Fragment {

    FragmentTranslateResultBinding binding;
    Fragment fragment;
    DashboardActivity activity;
    LanguagesModel input, output;
    boolean isZoomed = false;
    TTS tts;


    public TranslateResultFragment(Fragment fragment, LanguagesModel inSelectedLanguage, LanguagesModel outSelectedLanguage) {
        this.fragment = fragment;
        input = inSelectedLanguage;
        output = outSelectedLanguage;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_translate_result, container, false);

        activity = (DashboardActivity) requireActivity();

//        AdUtils.showNativeAd(activity, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), binding.adsView, false, activity.getDrawable(R.drawable.img_voice_ads));
        AdUtils.showNativeAd(activity, binding.adsView0, false);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();


        AdUtils.loadInitialInterstitialAds(activity);
        AdUtils.loadAppOpenAds(activity);

        AdUtils.loadInitialNativeList(activity);

        binding.btnBack.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.openFragment(fragment);
            });
        });
        activity.getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AdUtils.showInterstitialAd(activity, state_load -> {
                    activity.openFragment(fragment);
                });
            }
        });


        binding.tvFromLanguage.setText(input.getLanguageName());
        binding.tvToLanguage.setText(output.getLanguageName());

        binding.tvText.setText(TEXTEXTRACT);

        tts = new TTS(activity, output.getLanguageCode());
        getSingleTranslate(input, output, TEXTEXTRACT).observe(getViewLifecycleOwner(), data->{
            if (data != null){
                binding.tvResult.setText(data);
            }
        });

        binding.btnSettings.setOnClickListener(view -> {
            activity.drawerOpen();
        });

        binding.btnZoom.setOnClickListener(view -> {
            if (isZoomed){
                binding.tvResult.setTextSize(14);
            } else {
                binding.tvResult.setTextSize(24);
            }
            isZoomed = !isZoomed;
//            AdUtils.showInterstitialAd(activity, state_load -> {
//                startActivity(new Intent(activity, ZoomActivity.class).putExtra("data", binding.tvResult.getText().toString().trim()));
//            });
        });

        binding.btnShare.setOnClickListener(view -> {
            String textToShare = binding.tvResult.getText().toString().trim();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, textToShare);
            intent.setType("text/plain");
            startActivity(intent);
        });

        binding.btnSpeaker.setOnClickListener(view -> {
            String textToSpeak = binding.tvResult.getText().toString().trim();
            tts.speakText(textToSpeak);
        });

        binding.btnCopy.setOnClickListener(view -> {
            String textToCopy = binding.tvResult.getText().toString().trim();
            copyToClipboard(activity, textToCopy);
        });

        binding.btnZoom1.setOnClickListener(view -> {
            if (isZoomed){
                binding.tvResult.setTextSize(14);
            } else {
                binding.tvResult.setTextSize(24);
            }
            isZoomed = !isZoomed;
//            AdUtils.showInterstitialAd(activity, state_load -> {
//                startActivity(new Intent(activity, ZoomActivity.class).putExtra("data", binding.tvResult.getText().toString().trim()));
//            });
        });

        binding.btnShare1.setOnClickListener(view -> {
            String textToShare = binding.tvText.getText().toString().trim();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, textToShare);
            intent.setType("text/plain");
            startActivity(intent);
        });

        binding.btnSpeaker1.setOnClickListener(view -> {
            String textToSpeak = binding.tvText.getText().toString().trim();
            tts.speakText(textToSpeak);
        });

        binding.btnCopy1.setOnClickListener(view -> {
            String textToCopy = binding.tvText.getText().toString().trim();
            copyToClipboard(activity, textToCopy);
        });
    }
}