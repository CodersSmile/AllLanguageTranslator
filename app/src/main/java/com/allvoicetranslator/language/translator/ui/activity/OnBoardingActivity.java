package com.allvoicetranslator.language.translator.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import com.allvoicetranslator.language.translator.R;
import com.allvoicetranslator.language.translator.adapter.OnBoardingAdapter;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdUtils;
import com.allvoicetranslator.language.translator.ads.Utils.Constants;
import com.allvoicetranslator.language.translator.databinding.ActivityOnBoardingBinding;
import com.allvoicetranslator.language.translator.databinding.DialogTmcBinding;
import com.allvoicetranslator.language.translator.models.DataModel;
import com.allvoicetranslator.language.translator.utils.SharePreferences;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {

    ActivityOnBoardingBinding binding;
    OnBoardingAdapter adapter;
    SharePreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding);

        preferences = new SharePreferences(getApplicationContext());

        adapter = new OnBoardingAdapter();
        adapter.submitList(getBoardList());
        binding.viewPager.setAdapter(adapter);

        setOnBoardingIndicator();
        setCurrentOnBoardingIndicators(0);

    }

    @Override
    protected void onResume() {
        super.onResume();

        AdUtils.loadInitialInterstitialAds(OnBoardingActivity.this);
        AdUtils.loadAppOpenAds(OnBoardingActivity.this);
        AdUtils.loadInitialNativeList(this);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnBoardingIndicators(position);
            }
        });

        binding.btnNext.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(OnBoardingActivity.this, state_load -> {
                Constants.hitCounter = 0;
                if (binding.viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                    binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
                } else {
                    binding.dotsLayout.setVisibility(View.GONE);
                    binding.viewPager.setVisibility(View.GONE);
                    binding.title.setVisibility(View.GONE);
                    binding.desc.setVisibility(View.GONE);
                    binding.btnNext.setVisibility(View.GONE);

                    binding.layout.setVisibility(View.VISIBLE);
                    binding.layoutCheck.setVisibility(View.VISIBLE);
                    binding.btnStart.setVisibility(View.VISIBLE);

                }
            });
        });

        SpannableString string = new SpannableString(binding.tvPrivacy.getText().toString().trim());
        ClickableSpan clickPrivacy = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dglobalmed.blogspot.com/p/privacy-policy_5.html"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.primary));
                ds.setUnderlineText(true);
            }
        };
        ClickableSpan clickTMC = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                openTMC();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.primary));
                ds.setUnderlineText(true);
            }
        };
        string.setSpan(clickPrivacy, 12, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(clickTMC, 29, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.tvPrivacy.setText(string);
        binding.tvPrivacy.setClickable(true);  //add clickable
        binding.tvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

        binding.btnStart.setOnClickListener(view -> {
            if (!binding.checkbox.isChecked()) {
                Toast.makeText(this, "Please accept Terms and Condition!", Toast.LENGTH_SHORT).show();
                return;
            }
            AdUtils.showInterstitialAd(this, state_load -> {
                preferences.setFirstRun(false);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            });
        });


    }

    private void openTMC() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OnBoardingActivity.this);
        DialogTmcBinding tmcBinding = DialogTmcBinding.inflate(LayoutInflater.from(this));
        builder.setView(tmcBinding.getRoot());
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        tmcBinding.btnOk.setOnClickListener(view -> dialog.dismiss());

    }

    private void setOnBoardingIndicator() {
        ImageView[] indicators = new ImageView[adapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_dot));
            indicators[i].setLayoutParams(layoutParams);
            binding.dotsLayout.addView(indicators[i]);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setCurrentOnBoardingIndicators(int index) {
        int childCount = binding.dotsLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.dotsLayout.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_dot_selected));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_dot));
            }
        }

        binding.title.setText(getBoardList().get(index).getName());
        binding.desc.setText(getBoardList().get(index).getValue());

    }

    private List<DataModel> getBoardList() {
        List<DataModel> list = new ArrayList<>();


        list.add(new DataModel(getString(R.string.title_1), R.drawable.img_onboard1, getString(R.string.desc_1), false));
        list.add(new DataModel(getString(R.string.title_2), R.drawable.img_onboard2, getString(R.string.desc_2), false));

        return list;
    }

}