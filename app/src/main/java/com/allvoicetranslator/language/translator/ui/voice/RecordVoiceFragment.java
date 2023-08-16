package com.allvoicetranslator.language.translator.ui.voice;

import static android.app.Activity.RESULT_OK;
import static com.allvoicetranslator.language.translator.utils.Const.TEXTEXTRACT;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.allvoicetranslator.language.translator.R;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdUtils;
import com.allvoicetranslator.language.translator.databinding.FragmentRecordVoiceBinding;
import com.allvoicetranslator.language.translator.models.LanguagesModel;
import com.allvoicetranslator.language.translator.ui.activity.DashboardActivity;

import java.util.ArrayList;
import java.util.Objects;

public class RecordVoiceFragment extends Fragment {

    FragmentRecordVoiceBinding binding;
    Fragment fragment;
    DashboardActivity activity;
    LanguagesModel input, output;


    public RecordVoiceFragment(Fragment fragment, LanguagesModel inSelectedLanguage, LanguagesModel outSelectedLanguage) {
        this.fragment = fragment;
        input = inSelectedLanguage;
        output = outSelectedLanguage;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record_voice, container, false);

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


        binding.btnSettings.setOnClickListener(view -> {
            activity.drawerOpen();
        });

        binding.btnRecord.setOnClickListener(view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, input.getLanguageCode());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

            try {
                record.launch(intent);
            } catch (Exception e) {
                Toast.makeText(activity, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnRetake.setOnClickListener(view -> {
            binding.etText.setText("");
            binding.btnRecord.setVisibility(View.VISIBLE);
            binding.btnTranslate.setVisibility(View.GONE);
            binding.btnRetake.setVisibility(View.GONE);

        });

        binding.btnTranslate.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.openFragment(new TranslateResultFragment(new RecordVoiceFragment(new VoiceFragment(), input, output), input, output));
            });
        });

    }

    ActivityResultLauncher<Intent> record = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            ArrayList<String> strings = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            binding.etText.setText(Objects.requireNonNull(strings).get(0));
            TEXTEXTRACT = binding.etText.getText().toString().trim();
            if (!TEXTEXTRACT.isEmpty()){
                binding.btnRecord.setVisibility(View.GONE);
                binding.btnTranslate.setVisibility(View.VISIBLE);
                binding.btnRetake.setVisibility(View.VISIBLE);
            }
        }
    });
}