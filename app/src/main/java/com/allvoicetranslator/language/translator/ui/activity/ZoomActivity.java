package com.allvoicetranslator.language.translator.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.allvoicetranslator.language.translator.R;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdUtils;
import com.allvoicetranslator.language.translator.databinding.ActivityZoomBinding;

public class ZoomActivity extends AppCompatActivity {

    ActivityZoomBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_zoom);
//        Utility.setFullScreen(ZoomActivity.this);

        String data = getIntent().getStringExtra("data");

        binding.tvData.setText(data);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AdUtils.showInterstitialAd(this, state_load -> {
            finish();
        });
    }
}