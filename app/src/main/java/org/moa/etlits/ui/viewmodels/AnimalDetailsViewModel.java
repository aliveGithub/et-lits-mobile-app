package org.moa.etlits.ui.viewmodels;

import android.app.Application;

import org.moa.etlits.data.models.AnimalSearchResult;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.data.repositories.AnimalRepository;
import org.moa.etlits.data.repositories.CategoryValueRepository;
import org.moa.etlits.utils.Constants;

import java.util.List;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AnimalDetailsViewModel extends ViewModel {
    private AnimalRepository animalRepository;
    private CategoryValueRepository categoryValueRepository;
    private LiveData<AnimalSearchResult> animalSearchResult;
    private LiveData<List<CategoryValue>> categoryValueList;
    private MediatorLiveData<Pair<AnimalSearchResult, List<CategoryValue>>> animalData = new MediatorLiveData<>();
    AnimalDetailsViewModel(Application application, String animalId) {
        animalRepository = new AnimalRepository(application);
        categoryValueRepository = new CategoryValueRepository(application);
        animalSearchResult = animalRepository.loadByAnimalId(animalId);
        categoryValueList = categoryValueRepository.loadByTypes(new String[]{Constants.CATEGORY_KEY_BREEDS,Constants.CATEGORY_KEY_SEX});

        animalData.addSource(animalSearchResult, result -> {
            updateAnimalData(animalSearchResult.getValue(), categoryValueList.getValue());
        });

        animalData.addSource(categoryValueList, result -> {
            updateAnimalData(animalSearchResult.getValue(), categoryValueList.getValue());
        });
    }

    private void updateAnimalData(AnimalSearchResult animalRegistration, List<CategoryValue> categoryValueList) {
        if (animalRegistration != null && categoryValueList != null) {
            animalData.setValue(new Pair<>(animalRegistration, categoryValueList));
        }
    }
    public LiveData<Pair<AnimalSearchResult, List<CategoryValue>>> getAnimalData() {
        return animalData;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private Application application;
        private String animalId;

        public Factory(Application application, String animalId) {
            this.application = application;
            this.animalId = animalId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new AnimalDetailsViewModel(application, animalId);
        }
    }
}