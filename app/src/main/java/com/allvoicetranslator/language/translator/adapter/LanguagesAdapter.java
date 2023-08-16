package com.allvoicetranslator.language.translator.adapter;

import static com.allvoicetranslator.language.translator.utils.Const.SELECTED_LANGUAGE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import com.allvoicetranslator.language.translator.databinding.ViewLanguagesListBinding;
import com.allvoicetranslator.language.translator.models.LanguagesModel;

public class LanguagesAdapter extends ListAdapter<LanguagesModel, LanguagesAdapter.ViewHolder> {

    int selected = -1;
    String type;

    public LanguagesAdapter(String type) {
        super(diffCallback);
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewLanguagesListBinding binding = ViewLanguagesListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LanguagesModel model = getItem(position);

        holder.binding.name.setText(model.getLanguageName());

        holder.itemView.setOnClickListener(view -> {
            if (selected != -1){
                getCurrentList().get(selected).setSelected(false);
            }
            getCurrentList().get(holder.getAdapterPosition()).setSelected(true);
            selected = holder.getAdapterPosition();
            SELECTED_LANGUAGE = model;
            notifyDataSetChanged();
        });

        if (model.isSelected())
            holder.binding.image.setVisibility(View.VISIBLE);
        else
            holder.binding.image.setVisibility(View.INVISIBLE);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewLanguagesListBinding binding;
        public ViewHolder(@NonNull ViewLanguagesListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

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
}
