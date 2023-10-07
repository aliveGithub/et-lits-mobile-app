/*
 * Copyright (C) 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.moa.etlits.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.Animal;
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
