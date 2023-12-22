package org.moa.etlits.ui.adapters;


import android.view.ViewGroup;

import org.moa.etlits.data.models.AnimalSearchResult;
import org.moa.etlits.data.models.CategoryValue;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;


public class AnimalViewListAdapter extends ListAdapter<AnimalSearchResult, AnimalViewViewHolder> {
    private AnimalItemEventsListener animalItemEventsListener;
    private List<CategoryValue> categoryValueList = new ArrayList<>();

    public AnimalViewListAdapter(@NonNull DiffUtil.ItemCallback<AnimalSearchResult> diffCallback, AnimalItemEventsListener animalItemEventsListener) {
        super(diffCallback);
        this.animalItemEventsListener = animalItemEventsListener;
    }

    public void setCategoryValueList(List<CategoryValue> categoryValueList) {
        this.categoryValueList = categoryValueList;
    }

    @Override
    public AnimalViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return AnimalViewViewHolder.create(parent, animalItemEventsListener);
    }

    @Override
    public void onBindViewHolder(AnimalViewViewHolder holder, int position) {
        AnimalSearchResult current = getItem(position);
        holder.bind(current, categoryValueList);
    }

    public static class AnimalRegDiff extends DiffUtil.ItemCallback<AnimalSearchResult> {
        @Override
        public boolean areItemsTheSame(@NonNull AnimalSearchResult oldItem, @NonNull AnimalSearchResult newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull AnimalSearchResult oldItem, @NonNull AnimalSearchResult newItem) {
            return String.valueOf(oldItem.getId()).equals(String.valueOf(newItem.getId()));
        }
    }

    public interface AnimalItemEventsListener {
        void onAnimalItemClick(int position);
    }
}
