package com.allvoicetranslator.language.translator.ui.fragment;

import static com.allvoicetranslator.language.translator.utils.Const.INPUT;
import static com.allvoicetranslator.language.translator.utils.Const.MULTI_OUTPUT;
import static com.allvoicetranslator.language.translator.utils.Const.SELECTED_LANGUAGE;
import static com.allvoicetranslator.language.translator.utils.Utility.addLanguages;
import static com.allvoicetranslator.language.translator.utils.Utility.getLanguages;
import static com.allvoicetranslator.language.translator.utils.Utility.hideKeyboard;

import android.os.Bundle;
import android.text.Editable;
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
import com.allvoicetranslator.language.translator.adapter.LanguagesAdapter;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdUtils;
import com.allvoicetranslator.language.translator.databinding.FragmentLanguageBinding;
import com.allvoicetranslator.language.translator.models.LanguagesModel;
import com.allvoicetranslator.language.translator.ui.activity.DashboardActivity;
import com.allvoicetranslator.language.translator.utils.Const;

import java.util.ArrayList;
import java.util.List;

public class LanguageFragment extends Fragment {

    FragmentLanguageBinding binding;
    DashboardActivity activity;
    Fragment fragment;
    String type;
    LanguagesAdapter adapter;
    List<LanguagesModel> modelList = new ArrayList<>();

    public LanguageFragment(Fragment fragment, String type) {
        this.fragment = fragment;
        this.type = type;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_language, container, false);

        activity = (DashboardActivity) requireActivity();
        activity.hideBottom();

        AdUtils.showNativeAd(activity, binding.adsView, true);
        AdUtils.showNativeAd(activity, binding.adsView0, false);


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        AdUtils.loadInitialInterstitialAds(activity);
        AdUtils.loadAppOpenAds(activity);
        AdUtils.loadInitialNativeList(activity);

        adapter = new LanguagesAdapter(type);
        binding.recyclerView.setAdapter(adapter);
        setAdapter();

        binding.btnBack.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.openFragment(fragment);
                activity.visibleBottom();
            });
        });

        activity.getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AdUtils.showInterstitialAd(activity, state_load -> {
                    activity.openFragment(fragment);
                    activity.visibleBottom();
                });
            }
        });


        binding.btnNext.setOnClickListener(view -> {

            if (SELECTED_LANGUAGE == null){
                Toast.makeText(activity, "Please select a Language to proceed.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (type.equals(INPUT)) Const.IN_SELECTED_LANGUAGE = SELECTED_LANGUAGE;
            else if (type.equals(MULTI_OUTPUT)) addLanguages(SELECTED_LANGUAGE);
            else Const.OUT_SELECTED_LANGUAGE = SELECTED_LANGUAGE;

            hideKeyboard(activity);
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.openFragment(fragment);
                activity.visibleBottom();
            });

        });

        binding.etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    search(s.toString().trim());
            }
        });

    }

    private void setAdapter() {
        getLanguages().observe(getViewLifecycleOwner(), data ->{
            if (data.size() > 0) {
                modelList.addAll(data);
                adapter.submitList(data);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SELECTED_LANGUAGE = null;
    }

    private void search(String val) {
        List<LanguagesModel> list = new ArrayList<>();
        for (LanguagesModel model : modelList){
            if (model.getLanguageName().toLowerCase().contains(val.toLowerCase()))
                list.add(model);
        }
        adapter.submitList(list);
    }

}