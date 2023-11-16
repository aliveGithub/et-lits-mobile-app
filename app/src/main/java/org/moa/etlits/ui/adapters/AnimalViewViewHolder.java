package org.moa.etlits.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.AnimalSearchResult;
import org.moa.etlits.utils.Constants;
import org.moa.etlits.utils.DateUtils;

import androidx.recyclerview.widget.RecyclerView;

class AnimalViewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView tvAnimalId;
    private final TextView tvEid;
    private final TextView tvSex;
    private final TextView tvBreed;
    private final TextView tvAge;
    private final TextView tvSpecies;
    private final TextView tvEvent;
    private final TextView tvEventDate;

    private final ImageView ivAnimalImage;


    private AnimalViewListAdapter.AnimalItemEventsListener animalItemEventsListener;
    private AnimalViewViewHolder(View itemView, AnimalViewListAdapter.AnimalItemEventsListener animalItemEventsListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.animalItemEventsListener = animalItemEventsListener;
        tvAnimalId = itemView.findViewById(R.id.tv_animal_id);
        tvEid = itemView.findViewById(R.id.tv_eid);
        tvSex = itemView.findViewById(R.id.tv_sex);
        tvBreed = itemView.findViewById(R.id.tv_breed);
        tvAge = itemView.findViewById(R.id.tv_age);
        tvSpecies = itemView.findViewById(R.id.tv_species);
        tvEvent = itemView.findViewById(R.id.tv_event);
        tvEventDate = itemView.findViewById(R.id.tv_event_date);
        ivAnimalImage = itemView.findViewById(R.id.iv_animal_image);
    }

    public void bind(AnimalSearchResult animal) {
        tvAnimalId.setText(animal.getAnimalId());
        tvEid.setText("EID " + animal.getEid());
        tvSex.setText(getSex(animal.getSex()));
        tvBreed.setText(animal.getBreed());
        tvAge.setText(tvAge.getContext().getString(R.string.animal_reg_age_months, String.valueOf(animal.getAge())));
        tvSpecies.setText(animal.getSpecies());
        tvEventDate.setText(animal.getLastEventDate() != null ? DateUtils.formatDate(animal.getLastEventDate()) : "");

        if (animal.getLasEvent() != null) {
            tvEvent.setText(animal.getLasEvent());
        } else {
            tvEvent.setText(R.string.animal_list_status_pending_sync);
        }
        if (animal.isDead()) {
            ivAnimalImage.setImageResource(R.drawable.ic_animal_filled_dead);
        } else {
            ivAnimalImage.setImageResource(R.drawable.ic_animal_filled);
        }
    }

    private String getEvent(String event) {
        String eventStatus = Constants.EVENT_STATUS_MAP.get(event);
        return eventStatus != null ? eventStatus : event;
    }

    private String getSex(String value) {
        if (value != null && value.length() > 2 && value.startsWith("cs")) {
            return String.valueOf(value.substring(2).charAt(0));
        }

        return value;
    }


    public static AnimalViewViewHolder create(ViewGroup parent, AnimalViewListAdapter.AnimalItemEventsListener animalItemEventsListener) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_animals_item, parent, false);

        return new AnimalViewViewHolder(view, animalItemEventsListener);
    }

        @Override
        public void onClick(View v) {
            if(animalItemEventsListener != null) {
                animalItemEventsListener.onAnimalItemClick(getAdapterPosition());
            }
        }

}
