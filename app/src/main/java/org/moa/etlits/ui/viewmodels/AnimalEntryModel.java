package org.moa.etlits.ui.viewmodels;

import android.app.Application;
import android.text.TextUtils;

import org.moa.etlits.R;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.data.repositories.CategoryValueRepository;
import org.moa.etlits.ui.validation.AnimalFormState;
import org.moa.etlits.utils.Constants;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AnimalEntryModel extends ViewModel {

    private MutableLiveData<AnimalFormState> animalFormState = new MutableLiveData<>();
    private CategoryValueRepository categoryValueRepository;
    private LiveData<List<CategoryValue>> breedList;
    private LiveData<List<CategoryValue>> sexList;

    private static final String REGEX_PATTERN = "^ET \\d{10}$";

    AnimalEntryModel(Application application) {
        this.categoryValueRepository = new CategoryValueRepository(application);
        breedList = this.categoryValueRepository.loadByType(Constants.CATEGORY_KEY_BREEDS);
        sexList = this.categoryValueRepository.loadByType(Constants.CATEGORY_KEY_SEX);

    }

    public LiveData<List<CategoryValue>> getBreedList() {
        return breedList;
    }

    public LiveData<List<CategoryValue>> getSexList() {
        return sexList;
    }

    public LiveData<AnimalFormState> getAnimalFormState() {
        return animalFormState;
    }


    public void dataChanged(String animalId, String sex, String breed,Integer age, boolean dead, String seller) {
        TextUtils.isEmpty(animalId);
        AnimalFormState newAnimalFormState = new AnimalFormState();
        if (isEmpty(animalId)) {
            newAnimalFormState.setAnimalIdError(R.string.animal_reg_animal_id_required);
        } else {
            if (!isValidAnimalId(animalId)) {
                newAnimalFormState.setAnimalIdError(R.string.animal_reg_animal_id_invalid);
            }
        }

        if (isEmpty(sex)){
            newAnimalFormState.setSexError(R.string.animal_reg_sex_required);
        }

        if (isEmpty(breed)){
            newAnimalFormState.setBreedError(R.string.animal_reg_breed_required);
        }

        if (isEmpty(age)){
            newAnimalFormState.setAgeError(R.string.animal_reg_age_required);
        }

       animalFormState.setValue(newAnimalFormState);
    }

    private boolean isValidAnimalId(String animalId) {
        if (animalId == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(REGEX_PATTERN);
        Matcher matcher = pattern.matcher(animalId);
        return matcher.matches();
    }

    private boolean isEmpty(String value) {
        return value != null && value.trim().length() > 0;
    }

    private boolean isEmpty(Integer value) {
        return value != null && value > 0;
    }

    public static class AnimalDataEntryViewModelFactory implements ViewModelProvider.Factory {
        private Application application;

        public AnimalDataEntryViewModelFactory(Application application) {
            this.application = application;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new AnimalEntryModel(application);
        }
    }
}