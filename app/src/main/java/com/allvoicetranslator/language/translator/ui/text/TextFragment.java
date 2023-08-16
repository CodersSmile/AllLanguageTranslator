package com.allvoicetranslator.language.translator.ui.text;

import static com.allvoicetranslator.language.translator.utils.Const.INPUT;
import static com.allvoicetranslator.language.translator.utils.Const.IN_SELECTED_LANGUAGE;
import static com.allvoicetranslator.language.translator.utils.Const.OUTPUT;
import static com.allvoicetranslator.language.translator.utils.Const.OUT_SELECTED_LANGUAGE;
import static com.allvoicetranslator.language.translator.utils.Const.SELECTED_LANGUAGE;
import static com.allvoicetranslator.language.translator.utils.Utility.copyToClipboard;
import static com.allvoicetranslator.language.translator.utils.Utility.getSingleTranslate;
import static com.allvoicetranslator.language.translator.utils.Utility.hideKeyboard;
import static com.allvoicetranslator.language.translator.utils.Utility.pasteFromClipboard;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.allvoicetranslator.language.translator.R;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdUtils;
import com.allvoicetranslator.language.translator.databinding.FragmentTextBinding;
import com.allvoicetranslator.language.translator.ui.activity.DashboardActivity;
import com.allvoicetranslator.language.translator.ui.fragment.LanguageFragment;
import com.allvoicetranslator.language.translator.utils.TTS;

public class TextFragment extends Fragment {

    FragmentTextBinding binding;
    DashboardActivity activity;
    boolean isFocused = false, isZoomed = false;
    TTS tts;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_text, container, false);

        activity = (DashboardActivity) requireActivity();

        AdUtils.showNativeAd(activity,binding.adsView, true);
//        AdUtils.showNativeAd(activity, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), binding.adsView0, false, activity.getDrawable(R.drawable.img_text_ads0));

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

        binding.btnBack.setOnClickListener(view -> activity.finish());
        activity.getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AdUtils.showInterstitialAd(activity, state_load -> {
                    activity.finish();
                });
            }
        });

        binding.btnSettings.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.drawerOpen();
            });

        });

        binding.btnSwap.setOnClickListener(view -> {
            SELECTED_LANGUAGE = IN_SELECTED_LANGUAGE;
            IN_SELECTED_LANGUAGE = OUT_SELECTED_LANGUAGE;
            OUT_SELECTED_LANGUAGE = SELECTED_LANGUAGE;

            binding.btnInput.setText(IN_SELECTED_LANGUAGE.getLanguageName());
            binding.btnOutput.setText(OUT_SELECTED_LANGUAGE.getLanguageName());
            SELECTED_LANGUAGE = null;
        });

        binding.btnInput.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.openFragment(new LanguageFragment(new TextFragment(), INPUT));
            });
        });

        binding.btnOutput.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.openFragment(new LanguageFragment(new TextFragment(), OUTPUT));
            });
        });

        binding.etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    binding.btnTranslate.setBackgroundResource(R.drawable.btn_background_enabled);
                } else
                    binding.btnTranslate.setBackgroundResource(R.drawable.btn_background_disabled);
            }
        });

        binding.btnTranslate.setOnClickListener(view -> {
            String text = binding.etText.getText().toString().trim();

            if (TextUtils.isEmpty(text)) {
                Toast.makeText(activity, getString(R.string.please_type_something_to_translate), Toast.LENGTH_SHORT).show();
                return;
            }

//            binding.layout1.setVisibility(View.GONE);
            binding.layout2.setVisibility(View.VISIBLE);

            hideKeyboard(activity);
            setTranslate(text);
            int newY = binding.scroll.getScrollY() + 900;
            ObjectAnimator animator = ObjectAnimator.ofInt(binding.scroll, "scrollY", newY);
            animator.setDuration(1000);
            animator.start();

        });

        binding.btnPaste.setOnClickListener(view -> {
            String text = pasteFromClipboard(activity);
            binding.etText.setText(text);
        });

        binding.btnZoom.setOnClickListener(view -> {
            if (isZoomed) {
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tts != null) tts.stopTTS();
    }

    private void setTranslate(String text) {
        tts = new TTS(activity, OUT_SELECTED_LANGUAGE.getLanguageCode());
        binding.tvToLanguage.setText(OUT_SELECTED_LANGUAGE.getLanguageName());
        getSingleTranslate(IN_SELECTED_LANGUAGE, OUT_SELECTED_LANGUAGE, text).observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                binding.tvResult.setText(data);
            }
        });

    }

}