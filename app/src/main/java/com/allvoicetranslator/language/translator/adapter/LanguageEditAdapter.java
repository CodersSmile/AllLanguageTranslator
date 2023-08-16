package com.allvoicetranslator.language.translator.adapter;

import static com.allvoicetranslator.language.translator.utils.Const.MULTI_OUTPUT;
import static com.allvoicetranslator.language.translator.utils.Const.languagesModelList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdUtils;
import com.allvoicetranslator.language.translator.databinding.ViewLangListBinding;
import com.allvoicetranslator.language.translator.models.LanguagesModel;
import com.allvoicetranslator.language.translator.ui.activity.DashboardActivity;
import com.allvoicetranslator.language.translator.ui.fragment.LanguageFragment;
import com.allvoicetranslator.language.translator.ui.multi.MultiFragment;

public class LanguageEditAdapter extends ListAdapter<LanguagesModel, LanguageEditAdapter.ViewHolder> {

    static DiffUtil.ItemCallback<LanguagesModel> diffCallback = new DiffUtil.ItemCallback<LanguagesModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull LanguagesModel oldItem, @NonNull LanguagesModel newItem) {
            return Objects.equals(oldItem.getLanguageName(), newItem.getLanguageName()) || Objects.equals(oldItem.isSelected(), newItem.isSelected());
        }

        @Override
        public boolean areContentsTheSame(@NonNull LanguagesModel oldItem, @NonNull LanguagesModel newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };
    DashboardActivity activity;

    public LanguageEditAdapter(DashboardActivity activity) {
        super(diffCallback);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewLangListBinding binding = ViewLangListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LanguagesModel model = getItem(position);

        holder.binding.tvLang.setText(model.getLanguageName());

        if (position == (languagesModelList.size() - 1)) {
            holder.binding.btnAdd.setVisibility(View.VISIBLE);
        } else {
            holder.binding.btnAdd.setVisibility(View.GONE);
        }

        holder.binding.btnRemove.setOnClickListener(view -> {
            if (languagesModelList.size() > 1) {
                languagesModelList.remove(model);
                notifyDataSetChanged();
            }
        });

        holder.binding.btnAdd.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.openFragment(new LanguageFragment(new MultiFragment(), MULTI_OUTPUT));
            });
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewLangListBinding binding;

        public ViewHolder(@NonNull ViewLangListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

}
