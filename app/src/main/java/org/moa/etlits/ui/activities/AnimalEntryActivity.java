package org.moa.etlits.ui.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.databinding.ActivityAnimalEntryBinding;
import org.moa.etlits.ui.viewmodels.AnimalEntryModel;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

public class AnimalEntryActivity extends AppCompatActivity {
    private ActivityAnimalEntryBinding binding;
    private AnimalEntryModel viewModel;

    private boolean breedSpinnerInitialized = false;
    private boolean sexSpinnerInitialized = false;

    public static final String ADD_ANIMAL_RESULT = "ADD_ANIMAL_RESULT";
    public static final String ADD_ANOTHER_ANIMAL = "ADD_ANOTHER_ANIMAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimalEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(AnimalEntryActivity.this, new AnimalEntryModel.AnimalDataEntryViewModelFactory(getApplication())).get(AnimalEntryModel.class);
        viewModel.getBreedList().observe(this, breeds -> {
            CategoryValue categoryValue = new CategoryValue();
            categoryValue.setValue(" ");
            breeds.add(0, categoryValue);
            binding.sBreed.setAdapter(new ArrayAdapter<>(AnimalEntryActivity.this, android.R.layout.simple_spinner_item, breeds));
        });

        viewModel.getAnimalFormState().observe(this, formState -> {
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

        viewModel.getSexList().observe(this, sexList -> {
            CategoryValue categoryValue = new CategoryValue();
            categoryValue.setValue(" ");
            sexList.add(0, categoryValue);
            binding.sSex.setAdapter(new ArrayAdapter<>(AnimalEntryActivity.this, android.R.layout.simple_spinner_item, sexList));
        });

        binding.btnDone.setOnClickListener(v -> {
            validateAllFields();
            if (viewModel.getAnimalFormState().getValue() != null && viewModel.getAnimalFormState().getValue().isDataValid()) {
                returnResult(false);
            }

        });

        binding.btnAddNextAnimal.setOnClickListener(v -> {
            if (viewModel.getAnimalFormState().getValue() != null && viewModel.getAnimalFormState().getValue().isDataValid()) {
                returnResult(true);
            }
        });

        setUpActionBar();
        setUpDataValidation();
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.animal_reg_animal_detail_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            if (upArrow != null) {
                upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark, getTheme()), PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
            }
        }
    }

    public void returnResult(boolean addAnotherAnimal) {
        Intent replyIntent = new Intent();

        Animal animal = new Animal();
        animal.setAnimalId(binding.etAnimalId.getText().toString().trim());

        if (binding.sBreed.getSelectedItem() != null) {
            CategoryValue v = (CategoryValue) binding.sBreed.getSelectedItem();
            animal.setBreed(v.getValueId());
        }

        if (binding.sSex.getSelectedItem() != null) {
            CategoryValue v = (CategoryValue) binding.sSex.getSelectedItem();
            animal.setSex(v.getValueId());
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

    private void setUpDataValidation() {

        binding.etAnimalId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.validateAnimalId(binding.etAnimalId.getText().toString());
            }
        });

        binding.etAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.validateAge(binding.etAge.getText().toString().isEmpty() ? null : Integer.parseInt(binding.etAge.getText().toString()));
            }
        });


        binding.sBreed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!breedSpinnerInitialized) {
                    breedSpinnerInitialized = true;
                    return;
                }

                viewModel.validateBreed(binding.sBreed.getSelectedItem() != null ? binding.sBreed.getSelectedItem().toString() : null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.sSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!sexSpinnerInitialized) {
                    sexSpinnerInitialized = true;
                    return;
                }

                viewModel.validateSex(binding.sSex.getSelectedItem() != null ? binding.sSex.getSelectedItem().toString() : null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void validateAllFields() {
        viewModel.validateAllFields(
                binding.etAnimalId.getText().toString(),
                binding.sSex.getSelectedItem() != null ? binding.sSex.getSelectedItem().toString() : null,
                binding.sBreed.getSelectedItem() != null ? binding.sBreed.getSelectedItem().toString() : null,
                binding.etAge.getText().toString().isEmpty() ? null : Integer.parseInt(binding.etAge.getText().toString()),
                binding.swDead.isChecked(),
                binding.etSeller.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}