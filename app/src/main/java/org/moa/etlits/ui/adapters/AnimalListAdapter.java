package org.moa.etlits.ui.adapters;


import android.view.ViewGroup;

import org.moa.etlits.data.models.Animal;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;


public class AnimalListAdapter extends ListAdapter<Animal, AnimalViewHolder> {
    private AnimalItemEventsListener animalItemEventsListener;

    public AnimalListAdapter(@NonNull DiffUtil.ItemCallback<Animal> diffCallback, AnimalItemEventsListener animalItemEventsListener) {
        super(diffCallback);
        this.animalItemEventsListener = animalItemEventsListener;
    }

    @Override
    public AnimalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return AnimalViewHolder.create(parent, animalItemEventsListener);
    }

    @Override
    public void onBindViewHolder(AnimalViewHolder holder, int position) {
        Animal current = getItem(position);
        holder.bind(current);
    }

    public static class AnimalDiff extends DiffUtil.ItemCallback<Animal> {

        @Override
        public boolean areItemsTheSame(@NonNull Animal oldItem, @NonNull Animal newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Animal oldItem, @NonNull Animal newItem) {
            return String.valueOf(oldItem.getId()).equals(String.valueOf(newItem.getId()));
        }
    }

    public interface AnimalItemEventsListener {
        void onAnimalItemClick(int position);
        void onAnimalItemDeleteClick(int position);
    }
}
