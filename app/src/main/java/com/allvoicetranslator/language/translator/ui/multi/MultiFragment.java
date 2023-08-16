package com.allvoicetranslator.language.translator.ui.multi;

import static com.allvoicetranslator.language.translator.utils.Const.INPUT;
import static com.allvoicetranslator.language.translator.utils.Const.IN_SELECTED_LANGUAGE;
import static com.allvoicetranslator.language.translator.utils.Const.languagesModelList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.allvoicetranslator.language.translator.R;
import com.allvoicetranslator.language.translator.adapter.LanguageEditAdapter;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdUtils;
import com.allvoicetranslator.language.translator.databinding.FragmentMultiBinding;
import com.allvoicetranslator.language.translator.ui.activity.DashboardActivity;
import com.allvoicetranslator.language.translator.ui.fragment.LanguageFragment;

public class MultiFragment extends Fragment {

    FragmentMultiBinding binding;
    LanguageEditAdapter adapter;
    DashboardActivity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_multi, container, false);

        activity = (DashboardActivity) requireActivity();

        AdUtils.showNativeAd(activity,binding.adsView, true);
//        AdUtils.showNativeAd(activity, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), binding.adsView0, false, activity.getDrawable(R.drawable.img_multi_ads0));

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();


        AdUtils.loadInitialInterstitialAds(activity);
        AdUtils.loadAppOpenAds(activity);

        AdUtils.loadInitialNativeList(activity);
        binding.btnInput.setText(IN_SELECTED_LANGUAGE.getLanguageName());

        binding.btnInput.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.openFragment(new LanguageFragment(new MultiFragment(), INPUT));
            });
        });

        adapter = new LanguageEditAdapter(activity);
        binding.langRecyclerView.setAdapter(adapter);
        adapter.submitList(languagesModelList);

        binding.btnBack.setOnClickListener(view -> activity.openFirstFragment());

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
        binding.btnStart.setOnClickListener(view -> {
            activity.openFragment(new MultiResultFragment(new MultiFragment(), IN_SELECTED_LANGUAGE));
        });

    }


}