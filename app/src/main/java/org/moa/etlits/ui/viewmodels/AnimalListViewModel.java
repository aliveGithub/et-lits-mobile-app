package org.moa.etlits.ui.viewmodels;

import android.app.Application;

import org.moa.etlits.data.models.AnimalSearchResult;
import org.moa.etlits.data.models.AnimalListData;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.data.repositories.AnimalRepository;
import org.moa.etlits.data.repositories.CategoryValueRepository;
import org.moa.etlits.utils.Constants;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AnimalListViewModel extends AndroidViewModel {
    private AnimalRepository animalRepository;

    private CategoryValueRepository categoryValueRepository;
    private LiveData<List<AnimalSearchResult>> animalList;

    private LiveData<List<CategoryValue>> categoryValueList;

    private MediatorLiveData<AnimalListData> animalListMediator = new MediatorLiveData<>();
    public AnimalListViewModel(Application application) {
        super(application);
        animalRepository = new AnimalRepository(application);
        categoryValueRepository = new CategoryValueRepository(application);
        animalList = animalRepository.getAll();
        categoryValueList = categoryValueRepository.loadByTypes(new String[]{Constants.CATEGORY_KEY_BREEDS, Constants.CATEGORY_KEY_SEX});

        animalListMediator.addSource(animalList, animalSearchResults -> {
            updateAnimalListMediator();
        });

        animalListMediator.addSource(categoryValueList, categoryValues -> {
            updateAnimalListMediator();
        });
    }

    public MediatorLiveData<AnimalListData> getAnimalListMediator() {
        return animalListMediator;
    }

    private void updateAnimalListMediator() {
        AnimalListData animalSearchResultCombined = new AnimalListData();
        animalSearchResultCombined.setAnimalsList(animalList.getValue());
        animalSearchResultCombined.setCategoryValueList(categoryValueList.getValue());
        if (animalSearchResultCombined.getAnimalsList() != null && animalSearchResultCombined.getCategoryValueList() != null) {
            animalListMediator.setValue(animalSearchResultCombined);
        }
    }
    public LiveData<List<AnimalSearchResult>> getAnimalList() {
        return animalList;
    }
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private Application application;
        public Factory(Application application) {
            this.application = application;
        }
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new AnimalListViewModel(application);
        }
    }
}
