package com.allvoicetranslator.language.translator.ui.multi;

import static com.allvoicetranslator.language.translator.utils.Utility.getMultiTranslate;
import static com.allvoicetranslator.language.translator.utils.Utility.pasteFromClipboard;

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
import com.allvoicetranslator.language.translator.adapter.TranslateResultAdapter;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdUtils;
import com.allvoicetranslator.language.translator.databinding.FragmentMultiResultBinding;
import com.allvoicetranslator.language.translator.models.LanguagesModel;
import com.allvoicetranslator.language.translator.ui.activity.DashboardActivity;

public class MultiResultFragment extends Fragment {

    FragmentMultiResultBinding binding;
    TranslateResultAdapter adapter;
    DashboardActivity activity;
    Fragment fragment;
    LanguagesModel input;
    boolean isPaused = false;

    public MultiResultFragment(Fragment fragment, LanguagesModel inSelectedLanguage) {
        this.fragment = fragment;
        input = inSelectedLanguage;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_multi_result, container, false);

        activity = (DashboardActivity) requireActivity();

//        AdUtils.showNativeAd(activity, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), binding.adsView, false, activity.getDrawable(R.drawable.img_text_ads0));
        AdUtils.showNativeAd(activity,binding.adsView0, false);

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

        binding.btnSettings.setOnClickListener(view -> activity.drawerOpen());

        binding.tvFromLanguage.setText(input.getLanguageName());

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

            if (TextUtils.isEmpty(text)){
                Toast.makeText(activity, getString(R.string.please_type_something_to_translate), Toast.LENGTH_SHORT).show();
                return;
            }

            binding.btnTranslate.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            setAdapter(text);

        });

        binding.btnPaste.setOnClickListener(view -> {
            String text = pasteFromClipboard(activity);
            binding.etText.setText(text);
        });

    }

    private void setAdapter(String text) {
        adapter = new TranslateResultAdapter();
        binding.resultRecyclerView.setAdapter(adapter);
        binding.resultRecyclerView.setVisibility(View.VISIBLE);
        getMultiTranslate(input, text).observe(getViewLifecycleOwner(), data->{
            if (data.size() > 0){
                adapter.submitList(data);
                binding.btnTranslate.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

    }


}