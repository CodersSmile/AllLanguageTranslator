package com.allvoicetranslator.language.translator.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.allvoicetranslator.language.translator.BuildConfig;
import com.allvoicetranslator.language.translator.R;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdUtils;
import com.allvoicetranslator.language.translator.databinding.DialogTmcBinding;
import com.allvoicetranslator.language.translator.databinding.FragmentNavigationDrawerBinding;
import com.allvoicetranslator.language.translator.ui.activity.DashboardActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import java.util.Objects;

public class NavigationDashboardFragment extends Fragment {

    FragmentNavigationDrawerBinding binding;
    DashboardActivity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_navigation_drawer, container, false);

        activity = (DashboardActivity) requireActivity();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        AdUtils.loadInitialInterstitialAds(activity);
        AdUtils.loadAppOpenAds(activity);

        AdUtils.loadInitialNativeList(activity);
        binding.btnClose.setOnClickListener(view -> activity.closeDrawer());
        activity.getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                activity.closeDrawer();
            }
        });

        binding.btnTextTranslator.setOnClickListener(view -> {
            activity.closeDrawer();
            startActivity(new Intent(requireActivity(), DashboardActivity.class)
                    .putExtra("page", 0));
        });

        binding.btnVoiceTranslator.setOnClickListener(view -> {
            activity.closeDrawer();
            startActivity(new Intent(requireActivity(), DashboardActivity.class)
                    .putExtra("page", 1));

        });

        binding.btnPhotoTranslator.setOnClickListener(view -> {
            activity.closeDrawer();
            startActivity(new Intent(requireActivity(), DashboardActivity.class)
                    .putExtra("page", 2));

        });

        binding.btnMultiTranslator.setOnClickListener(view -> {
            activity.closeDrawer();
            startActivity(new Intent(requireActivity(), DashboardActivity.class)
                    .putExtra("page", 4));

        });

        binding.btnShare.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name)+" App Invitation");
            intent.putExtra(Intent.EXTRA_TEXT, "Say goodbye to language barriers! Communicate effortlessly in real-time by voice or text, with translations available in multiple languages.\n" +
                    "Enable seamless translation across multiple languages while also serving as your handy dictionary companion.\n" +
                    "Download the "+getString(R.string.app_name)+" app now!" +
                    "\nhttps://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            intent.setType("text/plain");
            startActivity(intent);
        });

        binding.btnPrivacy.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dglobalmed.blogspot.com/p/privacy-policy_5.html"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        binding.btnRateUs.setOnClickListener(view -> {
            reviewDialog();
        });

        binding.btnTmc.setOnClickListener(view -> {
            openTMC();
        });

    }

    private void openTMC() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        DialogTmcBinding tmcBinding = DialogTmcBinding.inflate(LayoutInflater.from(activity));
        builder.setView(tmcBinding.getRoot());
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        tmcBinding.btnOk.setOnClickListener(view -> dialog.dismiss());

    }


    private void reviewDialog() {
        ReviewManager manager = ReviewManagerFactory.create(requireActivity());
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(requireActivity(), reviewInfo);
                flow.addOnCompleteListener(task1 -> {

                });
            } else {
                // There was some problem, log or handle the error code.
                Log.e("reviewDialog: ", Objects.requireNonNull(task.getException()).getLocalizedMessage());
            }
        });

    }



}
