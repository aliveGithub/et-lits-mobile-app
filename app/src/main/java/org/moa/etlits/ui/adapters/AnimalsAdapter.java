package org.moa.etlits.ui.adapters;


import android.view.ViewGroup;

import org.moa.etlits.data.models.AnimalSearchResult;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;


public class AnimalsAdapter extends ListAdapter<AnimalSearchResult, AnimalsViewHolder> {
    private AnimalItemEventsListener animalItemEventsListener;

    public AnimalsAdapter(@NonNull DiffUtil.ItemCallback<AnimalSearchResult> diffCallback, AnimalItemEventsListener animalItemEventsListener) {
        super(diffCallback);
        this.animalItemEventsListener = animalItemEventsListener;
    }

    @Override
    public AnimalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return AnimalsViewHolder.create(parent, animalItemEventsListener);
    }

    @Override
    public void onBindViewHolder(AnimalsViewHolder holder, int position) {
        AnimalSearchResult current = getItem(position);
        holder.bind(current);
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
