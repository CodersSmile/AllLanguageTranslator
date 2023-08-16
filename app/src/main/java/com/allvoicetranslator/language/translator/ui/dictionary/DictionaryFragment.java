package com.allvoicetranslator.language.translator.ui.dictionary;

import static com.allvoicetranslator.language.translator.utils.Utility.getDefinition;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.allvoicetranslator.language.translator.R;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdUtils;
import com.allvoicetranslator.language.translator.databinding.FragmentDictionaryBinding;
import com.allvoicetranslator.language.translator.models.DictionaryModel;
import com.allvoicetranslator.language.translator.ui.activity.DashboardActivity;

import java.util.List;

public class DictionaryFragment extends Fragment {

    FragmentDictionaryBinding binding;
    DashboardActivity activity;
    boolean isStarted = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dictionary, container, false);

        activity = (DashboardActivity) requireActivity();

        AdUtils.showNativeAd(activity,binding.adsView, true);
//        AdUtils.showNativeAd(activity, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), binding.adsView0, false, activity.getDrawable(R.drawable.img_dictionary_ads0));

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        AdUtils.loadInitialInterstitialAds(activity);
        AdUtils.loadAppOpenAds(activity);

        AdUtils.loadInitialNativeList(activity);

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

        binding.etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()){
                    binding.btnClear.setVisibility(View.VISIBLE);
                } else
                    binding.btnClear.setVisibility(View.GONE);
            }
        });

        binding.btnClear.setOnClickListener(view -> {
            binding.etSearchBar.setText("");
            binding.imgSearch.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
            binding.layout.setVisibility(View.GONE);
            binding.tvNot.setVisibility(View.GONE);
            binding.layout.removeAllViews();
            isStarted = false;
        });

        binding.btnSearch.setOnClickListener(view -> {
            if (!isStarted){
                String string = binding.etSearchBar.getText().toString().trim();

                if (string.isEmpty()) {
                    binding.etSearchBar.setError("Please enter a value to search.");
                    binding.etSearchBar.requestFocus();
                    return;
                }

                isStarted = true;

                binding.imgSearch.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);

                setDefinition(string);
            } else
                Toast.makeText(activity, "Please wait to complete the previous search.", Toast.LENGTH_SHORT).show();

        });

    }

    private void setDefinition(String string) {
        binding.layout.removeAllViews();
        binding.tvNot.setVisibility(View.GONE);
        StringBuilder stringBuilder = new StringBuilder();
        getDefinition(string).observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                binding.tvSearch.setText(data.getWord());
                binding.layout.addView(binding.tvSearch);
                for (int i = 0; i < data.getMeanings().size(); i++) {
                    String speech = "\n" + data.getMeanings().get(i).getPartOfSpeech();

                    TextView tvSpeech = new TextView(activity);
                    TextView tvWords = new TextView(activity);

                    tvSpeech.setTextSize(14);
                    tvSpeech.setTextColor(activity.getColor(R.color.black_7));
                    tvSpeech.setTypeface(null, Typeface.ITALIC);

                    tvWords.setTextSize(14);
                    tvWords.setBackgroundResource(R.drawable.rect_round_stroke_12);
                    tvWords.setPadding(12, 6, 12, 6);
                    tvWords.setTextColor(activity.getColor(R.color.black_7));

                    List<String> syn = data.getMeanings().get(i).getSynonyms();
                    if (syn.size() > 0){
                        for (int j = 0; j < syn.size(); j++) {
                            if (syn.size()-1 == j) {
                                stringBuilder.append(syn.get(j));
                            } else stringBuilder.append(syn.get(j)).append(", ");
                        }
                        tvWords.setText("Synonyms: "+ stringBuilder);
                    } else tvWords.setVisibility(View.GONE);

                    tvSpeech.setText(speech);
                    binding.layout.addView(tvSpeech);

                    List<DictionaryModel.MeaningsDTO.DefinitionsDTO> definitionsDTO = data.getMeanings().get(i).getDefinitions();
                    for (int j = 0; j < definitionsDTO.size(); j++) {
                        TextView tvMeaning = new TextView(activity);
                        tvMeaning.setTextSize(16);
                        tvMeaning.setTextColor(activity.getColor(R.color.black_7));
                        tvMeaning.setText((j + 1) + ". " + definitionsDTO.get(j).getDefinition());

                        binding.layout.addView(tvMeaning);

                    }
                    binding.layout.addView(tvWords);
                }
                binding.layout.setVisibility(View.VISIBLE);
            } else {
                binding.tvNot.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
            isStarted = false;
        });
    }

}