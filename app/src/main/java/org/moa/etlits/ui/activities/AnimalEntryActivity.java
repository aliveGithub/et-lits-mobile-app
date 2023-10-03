package org.moa.etlits.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.moa.etlits.data.models.Animal;
import org.moa.etlits.databinding.ActivityAnimalEntryBinding;
import org.moa.etlits.ui.viewmodels.AnimalEntryModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class AnimalEntryActivity extends AppCompatActivity {
    private ActivityAnimalEntryBinding binding;
    private AnimalEntryModel animalDataEntryViewModel;

    public static final String ADD_ANIMAL_RESULT = "ADD_ANIMAL_RESULT";
    public static final String ADD_ANOTHER_ANIMAL = "ADD_ANOTHER_ANIMAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityAnimalEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        animalDataEntryViewModel = new ViewModelProvider(AnimalEntryActivity.this, new AnimalEntryModel.AnimalDataEntryViewModelFactory(getApplication())).get(AnimalEntryModel.class);
        animalDataEntryViewModel.getBreedList().observe(this, breeds -> {
            binding.sBreed.setAdapter(new ArrayAdapter<>(AnimalEntryActivity.this, android.R.layout.simple_spinner_item, breeds));
        });

        animalDataEntryViewModel.getAnimalFormState().observe(this, formState -> {
            if (formState == null) {
                return;
            }

            binding.btnDone.setEnabled(formState.isDataValid());
            binding.btnAddNextAnimal.setEnabled(formState.isDataValid());

            if (formState.getAnimalIdError() != null) {
                binding.etAnimalId.setError(getString(formState.getAnimalIdError()));
            }

            if (formState.getBreedError() != null) {
                if (binding.sBreed.getSelectedView() != null) {
                    TextView tv = (TextView) (binding.sBreed.getSelectedView());
                    tv.setError(getString(formState.getBreedError()));
                }
            }

            if (formState.getSexError() != null) {
                if (binding.sSex.getSelectedView() != null) {
                    TextView tv = (TextView) (binding.sSex.getSelectedView());
                    tv.setError(getString(formState.getSexError()));
                }
            }
            if (formState.getAgeError() != null) {
                binding.etAge.setError(getString(formState.getAgeError()));
            }
        });

        animalDataEntryViewModel.getSexList().observe(this, sexList -> {
            binding.sSex.setAdapter(new ArrayAdapter<>(AnimalEntryActivity.this, android.R.layout.simple_spinner_item, sexList));
        });

        binding.btnDone.setOnClickListener(v -> {
            returnResult(false);
        });

        binding.btnAddNextAnimal.setOnClickListener(v -> {
            returnResult(true);
        });

        validateInput();
    }

    public void returnResult(boolean addAnotherAnimal) {
        Intent replyIntent = new Intent();

        Animal animal = new Animal();
        animal.setAnimalId(binding.etAnimalId.getText().toString().trim());

        if (binding.sBreed.getSelectedItem() != null) {
            animal.setBreed(String.valueOf(binding.sBreed.getSelectedItem()));
        }

        if (binding.sSex.getSelectedItem() != null) {
            animal.setSex(binding.sSex.getSelectedItem().toString());
        }

        if (!binding.etAge.getText().toString().isEmpty()) {
           animal.setAge(Integer.parseInt(binding.etAge.getText().toString()));
        }

        animal.setDead(binding.swDead.isChecked());
        animal.setSeller(String.valueOf(binding.etSeller.getText()));

        replyIntent.putExtra(ADD_ANIMAL_RESULT, animal);
        replyIntent.putExtra(ADD_ANOTHER_ANIMAL, addAnotherAnimal);
        setResult(RESULT_OK, replyIntent);

        finish();
    }

    private void validateInput() {
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                dataChanged();
            }
        };
        binding.etAnimalId.addTextChangedListener(afterTextChangedListener);
        binding.etAge.addTextChangedListener(afterTextChangedListener);
        binding.sBreed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.sSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void dataChanged() {
        animalDataEntryViewModel.dataChanged(
                binding.etAnimalId.getText().toString(),
                binding.sBreed.getSelectedItem() != null ? binding.sBreed.getSelectedItem().toString() : null,
                binding.sSex.getSelectedItem() != null ? binding.sSex.getSelectedItem().toString() : null,
                binding.etAge.getText().toString().isEmpty() ? null : Integer.parseInt(binding.etAge.getText().toString()),
                binding.swDead.isChecked(),
                binding.etSeller.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}