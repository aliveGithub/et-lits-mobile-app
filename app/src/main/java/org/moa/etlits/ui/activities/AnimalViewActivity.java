package org.moa.etlits.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import org.moa.etlits.R;
import org.moa.etlits.data.repositories.AnimalRepository;
import org.moa.etlits.databinding.ActivityAnimalViewBinding;

public class AnimalViewActivity extends AppCompatActivity {
    private ActivityAnimalViewBinding binding = null;
    private AnimalRepository animalRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimalViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initActionBar();
        animalRepository = new AnimalRepository(this.getApplication());
        String animalId = getIntent().getStringExtra("animalId");
        animalRepository.loadByAnimalId(animalId).observe(this, animal -> {
            if (animal != null) {
                binding.tvAnimalId.setText(animal.getAnimalId());
                binding.tvSex.setText(getSex(animal.getSex()));
                binding.tvAge.setText(String.valueOf(animal.getAge()));
                binding.tvBreed.setText(animal.getBreed());
                binding.tvSpecies.setText(animal.getSpecies());
                binding.tvEstablishment.setText(animal.getEid());
                binding.tvTerminated.setText(animal.isDead() ? R.string.animal_view_terminated_yes : R.string.animal_view_terminated_no);
            }
        });
    }

    private String getSex(String value){
        if (value != null && value.length() > 2 && value.startsWith("cs")) {
            return String.valueOf(value.substring(2).charAt(0));
        }

        return value;
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.animal_view_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            if (upArrow != null) {
                upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark, getTheme()), PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}