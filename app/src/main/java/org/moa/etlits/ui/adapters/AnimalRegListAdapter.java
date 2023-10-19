package org.moa.etlits.ui.adapters;


import android.view.ViewGroup;

import org.moa.etlits.data.models.AnimalRegistration;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;


public class AnimalRegListAdapter extends ListAdapter<AnimalRegistration, AnimalRegViewHolder> {
    private AnimalRegItemEventsListener animalRegItemEventsListener;

    public AnimalRegListAdapter(@NonNull DiffUtil.ItemCallback<AnimalRegistration> diffCallback, AnimalRegItemEventsListener animalRegItemEventsListener) {
        super(diffCallback);
        this.animalRegItemEventsListener = animalRegItemEventsListener;
    }

    @Override
    public AnimalRegViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return AnimalRegViewHolder.create(parent, animalRegItemEventsListener);
    }

    @Override
    public void onBindViewHolder(AnimalRegViewHolder holder, int position) {
        AnimalRegistration current = getItem(position);
        holder.bind(current);
    }

    public static class AnimalRegDiff extends DiffUtil.ItemCallback<AnimalRegistration> {

        @Override
        public boolean areItemsTheSame(@NonNull AnimalRegistration oldItem, @NonNull AnimalRegistration newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull AnimalRegistration oldItem, @NonNull AnimalRegistration newItem) {
            return String.valueOf(oldItem.getId()).equals(String.valueOf(newItem.getId()));
        }
    }

    public interface AnimalRegItemEventsListener {
        void onAnimalRegItemClick(int position);
        void onAnimalRegItemDeleteClick(int position);
    }
}
