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
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.Animal;

import androidx.recyclerview.widget.RecyclerView;

class AnimalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView tvAnimalId;
    private final TextView tvSex;
    private final TextView tvBreed;
    private final TextView tvAge;

    private AnimalListAdapter.AnimalItemEventsListener animalItemEventsListener;
    private AnimalViewHolder(View itemView, AnimalListAdapter.AnimalItemEventsListener animalItemEventsListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.animalItemEventsListener = animalItemEventsListener;
        tvAnimalId = itemView.findViewById(R.id.tv_animal_id);
        tvSex = itemView.findViewById(R.id.tv_sex);
        tvBreed = itemView.findViewById(R.id.tv_breed);
        tvAge = itemView.findViewById(R.id.tv_age);

    }

    public void bind(Animal animal) {
        tvAnimalId.setText(String.valueOf(animal.getAnimalId()));
        tvSex.setText(animal.getSex());
        tvBreed.setText(animal.getBreed());
        tvAge.setText(tvAge.getContext().getString(R.string.animal_reg_age_months, String.valueOf(animal.getAge())));}

    public static AnimalViewHolder create(ViewGroup parent, AnimalListAdapter.AnimalItemEventsListener animalItemEventsListener) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_animal_item, parent, false);

        return new AnimalViewHolder(view, animalItemEventsListener);
    }

        @Override
        public void onClick(View v) {
           if(animalItemEventsListener != null) {
               animalItemEventsListener.onAnimalClick(getAdapterPosition());
           }
        }

}
