package org.moa.etlits.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.AnimalRegistration;
import org.moa.etlits.utils.DateUtils;

import androidx.recyclerview.widget.RecyclerView;

class AnimalRegViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView tvAnimalRegId;
    private final TextView tvHoldingEid;
    private final TextView tvDateIdentification;


    private AnimalRegListAdapter.AnimalRegItemEventsListener animalRegItemEventsListener;
    private AnimalRegViewHolder(View itemView, AnimalRegListAdapter.AnimalRegItemEventsListener animalRegItemEventsListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.animalRegItemEventsListener = animalRegItemEventsListener;
        tvAnimalRegId = itemView.findViewById(R.id.tv_animal_reg_id);
        tvHoldingEid = itemView.findViewById(R.id.tv_holding_eid);
        tvDateIdentification = itemView.findViewById(R.id.tv_date_identification);
        


    }

    public void bind(AnimalRegistration animalRegistration) {
        tvAnimalRegId.setText(String.valueOf(animalRegistration.getId()));
        tvHoldingEid.setText(animalRegistration.getHoldingGroundEid());
        if (animalRegistration.getDateIdentification() != null) {                   
            tvDateIdentification.setText(DateUtils.formatDate(animalRegistration.getDateIdentification()));
        }        
    }



    public static AnimalRegViewHolder create(ViewGroup parent, AnimalRegListAdapter.AnimalRegItemEventsListener animalRegItemEventsListener) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_animal_reg_item, parent, false);

        return new AnimalRegViewHolder(view, animalRegItemEventsListener);
    }

        @Override
        public void onClick(View v) {
            if(animalRegItemEventsListener != null) {
                animalRegItemEventsListener.onAnimalRegItemClick(getAdapterPosition());
            }
        }

}
