package org.moa.etlits.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.moa.etlits.data.models.Animal;
import org.moa.etlits.databinding.ActivityAddAnimalBinding;

public class AddAnimalActivity extends AppCompatActivity {
    private ActivityAddAnimalBinding binding;

    public static final String ADD_ANIMAL_RESULT = "ADD_ANIMAL_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityAddAnimalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnDone.setOnClickListener(v -> {
            returnResult();
        });
    }

    public void returnResult() {
        Intent replyIntent = new Intent();
        if (!binding.etAnimalId.getText().toString().isEmpty()) {
            Animal result = new Animal();
            result.setAnimalId(String.valueOf(binding.etAnimalId.getText()));
            replyIntent.putExtra(ADD_ANIMAL_RESULT, result);
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