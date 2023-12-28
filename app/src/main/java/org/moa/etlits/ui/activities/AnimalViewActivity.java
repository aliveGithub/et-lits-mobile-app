package org.moa.etlits.ui.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import org.moa.etlits.R;
import org.moa.etlits.data.models.AnimalSearchResult;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.databinding.ActivityAnimalViewBinding;
import org.moa.etlits.ui.viewmodels.AnimalDetailsViewModel;
import org.moa.etlits.utils.Constants;
import org.moa.etlits.utils.DateUtils;
import org.moa.etlits.utils.ViewUtils;

import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

public class AnimalViewActivity extends AppCompatActivity {
    private ActivityAnimalViewBinding binding = null;
    private AnimalDetailsViewModel animalViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimalViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initActionBar();

        String animalId = getIntent().getStringExtra("animalId");
        animalViewModel = new ViewModelProvider(
                this,
                new AnimalDetailsViewModel.Factory(getApplication(), animalId)
        ).get(AnimalDetailsViewModel.class);

        animalViewModel.getAnimalData().observe(this, combined -> {
            AnimalSearchResult animal = combined.first;
            List<CategoryValue> categoryValueList = combined.second;
            if (animal != null) {
                binding.tvAnimalId.setText(animal.getAnimalId());
                binding.tvSex.setText(ViewUtils.getCategoryLabel(animal.getSex(), categoryValueList, Constants.CATEGORY_KEY_SEX));
                binding.tvAge.setText(getString(R.string.animal_reg_age_months, String.valueOf(animal.getAge())));
                binding.tvBirthDate.setText(getDateOfBirth(animal.getAge()));
                binding.tvBreed.setText(ViewUtils.getCategoryLabel(animal.getBreed(), categoryValueList, Constants.CATEGORY_KEY_BREEDS));
                binding.tvSpecies.setText(animal.getSpecies());
                binding.tvEstablishment.setText(animal.getEid() + " - " + animal.getEstablishmentName());
                binding.tvTerminated.setText(animal.isDead() ? R.string.animal_view_terminated_yes : R.string.animal_view_terminated_no);
            }
        });
    }



    private String getDateOfBirth(int months) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -months);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return DateUtils.formatDate(cal.getTime());
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