package org.moa.etlits.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.moa.etlits.data.models.Animal;
import org.moa.etlits.databinding.ActivityAnimalDataEntryBinding;

import java.util.Calendar;

public class AnimalDataEntryActivity extends AppCompatActivity {
    private ActivityAnimalDataEntryBinding binding;

    public static final String ADD_ANIMAL_RESULT = "ADD_ANIMAL_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityAnimalDataEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnDone.setOnClickListener(v -> {
            returnResult();
        });
    }

    public void returnResult() {
        Intent replyIntent = new Intent();
        if (!binding.etAnimalId.getText().toString().isEmpty()) {

            Animal animal = new Animal();
            animal.setAnimalId(String.valueOf(binding.etAnimalId.getText()));

            if (binding.sBreed.getSelectedItem() != null) {
                animal.setBreed(String.valueOf(binding.sBreed.getSelectedItem()));
            }

            if (binding.sSex.getSelectedItem() != null) {
                animal.setSex(binding.sSex.getSelectedItem().toString());
            }

            if (!binding.etAge.getText().toString().isEmpty()) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -Integer.parseInt(binding.etAge.getText().toString()));
                animal.setDateBirth(calendar.getTime());
            }

            animal.setDead(binding.swDead.isChecked());
            animal.setSeller(String.valueOf(binding.etSeller.getText()));

            replyIntent.putExtra(ADD_ANIMAL_RESULT, animal);
            setResult(RESULT_OK, replyIntent);
        } else {
            setResult(RESULT_CANCELED, replyIntent);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}