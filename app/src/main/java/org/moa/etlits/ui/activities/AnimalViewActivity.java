package org.moa.etlits.ui.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import org.moa.etlits.R;
import org.moa.etlits.data.models.AnimalSearchResult;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.data.repositories.AnimalRepository;
import org.moa.etlits.data.repositories.CategoryValueRepository;
import org.moa.etlits.databinding.ActivityAnimalViewBinding;
import org.moa.etlits.utils.Constants;
import org.moa.etlits.utils.DateUtils;
import org.moa.etlits.utils.ViewUtils;

import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class AnimalViewActivity extends AppCompatActivity {
    private ActivityAnimalViewBinding binding = null;
    private AnimalRepository animalRepository;
    private CategoryValueRepository categoryValueRepository;

    private MediatorLiveData<Pair<AnimalSearchResult, List<CategoryValue>>> animalDataMediator = new MediatorLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimalViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initActionBar();

        String animalId = getIntent().getStringExtra("animalId");
        animalRepository = new AnimalRepository(this.getApplication());
        categoryValueRepository = new CategoryValueRepository(this.getApplication());

        LiveData<AnimalSearchResult> animalSearchResultLiveData = animalRepository.loadByAnimalId(animalId);
        LiveData<List<CategoryValue>> categoryValueListLiveData = categoryValueRepository.loadByTypes(new String[]{Constants.CATEGORY_KEY_BREEDS,Constants.CATEGORY_KEY_SEX});

        animalDataMediator.addSource(animalSearchResultLiveData, animalSearchResultx -> {
            updateAnimalDataMediator(animalSearchResultLiveData.getValue(), categoryValueListLiveData.getValue());
        });

        animalDataMediator.addSource(categoryValueListLiveData, categoryValueList -> {
            updateAnimalDataMediator(animalSearchResultLiveData.getValue(), categoryValueListLiveData.getValue());
        });

        animalDataMediator.observe(this, combined -> {
            AnimalSearchResult animal = combined.first;
            List<CategoryValue> categoryValueList = combined.second;
            if (animal != null) {
                binding.tvAnimalId.setText(animal.getAnimalId());
                binding.tvSex.setText(ViewUtils.getValue(animal.getSex(), categoryValueList));
                binding.tvAge.setText(getString(R.string.animal_reg_age_months, String.valueOf(animal.getAge())));
                binding.tvBirthDate.setText(getDateOfBirth(animal.getAge()));
                binding.tvBreed.setText(ViewUtils.getValue(animal.getBreed(), categoryValueList));
                binding.tvSpecies.setText(animal.getSpecies());
                binding.tvEstablishment.setText(animal.getEid() + " - " + animal.getEstablishmentName());
                binding.tvTerminated.setText(animal.isDead() ? R.string.animal_view_terminated_yes : R.string.animal_view_terminated_no);
            }
        });
    }

    private void updateAnimalDataMediator(AnimalSearchResult animalRegistration, List<CategoryValue> categoryValueList) {
        if (animalRegistration != null && categoryValueList != null) {
            animalDataMediator.setValue(new Pair<>(animalRegistration, categoryValueList));
        }
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