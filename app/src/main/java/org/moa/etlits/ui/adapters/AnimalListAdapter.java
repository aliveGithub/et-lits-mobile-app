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
        //return new AnimalViewHolder(parent, animalItemEventsListener);
    }

    @Override
    public void onBindViewHolder(AnimalViewHolder holder, int position) {
        /*Animal current = getItem(position);
        String tag = current.getTag() != null ? current.getTag() : "";
        String method = current.getMethod() != null ? current.getMethod() : "";
        android.text.format.DateFormat df = new android.text.format.DateFormat();

        String date = current.getDateIdentification() != null ? (String) df.format("yyyy-MM-dd", current.getDateIdentification()) : "";
        holder.bind(current.getTag() + ":" + method +  ":" + date);*/
    }

    public static class AnimalDiff extends DiffUtil.ItemCallback<Animal> {

        @Override
        public boolean areItemsTheSame(@NonNull Animal oldItem, @NonNull Animal newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Animal oldItem, @NonNull Animal newItem) {
            return true; //String.valueOf(oldItem.getId()).equals(String.valueOf(newItem.getId()));
        }
    }

    public interface AnimalItemEventsListener {
        void onAnimalClick(int position);
    }
}
