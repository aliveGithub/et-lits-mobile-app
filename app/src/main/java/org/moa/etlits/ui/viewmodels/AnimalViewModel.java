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

public class AnimalViewModel extends ViewModel {
    private AnimalRepository animalRepository;
    private CategoryValueRepository categoryValueRepository;
    private LiveData<AnimalSearchResult> animalSearchResultLiveData;
    private LiveData<List<CategoryValue>> categoryValueListLiveData;
    private MediatorLiveData<Pair<AnimalSearchResult, List<CategoryValue>>> animalDataMediator = new MediatorLiveData<>();
    AnimalViewModel(Application application, String animalId) {
        animalRepository = new AnimalRepository(application);
        categoryValueRepository = new CategoryValueRepository(application);
        animalSearchResultLiveData = animalRepository.loadByAnimalId(animalId);
        categoryValueListLiveData = categoryValueRepository.loadByTypes(new String[]{Constants.CATEGORY_KEY_BREEDS,Constants.CATEGORY_KEY_SEX});

        animalDataMediator.addSource(animalSearchResultLiveData, animalSearchResultx -> {
            updateAnimalDataMediator(animalSearchResultLiveData.getValue(), categoryValueListLiveData.getValue());
        });

        animalDataMediator.addSource(categoryValueListLiveData, categoryValueList -> {
            updateAnimalDataMediator(animalSearchResultLiveData.getValue(), categoryValueListLiveData.getValue());
        });
    }

    private void updateAnimalDataMediator(AnimalSearchResult animalRegistration, List<CategoryValue> categoryValueList) {
        if (animalRegistration != null && categoryValueList != null) {
            animalDataMediator.setValue(new Pair<>(animalRegistration, categoryValueList));
        }
    }
    public LiveData<Pair<AnimalSearchResult, List<CategoryValue>>> getAnimalDataMediator() {
        return animalDataMediator;
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
            return (T) new AnimalViewModel(application, animalId);
        }
    }
}