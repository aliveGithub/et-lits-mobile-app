package org.moa.etlits.ui.viewmodels;

import android.app.Application;

import org.moa.etlits.data.models.AnimalSearchResult;
import org.moa.etlits.data.repositories.AnimalRepository;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AnimalListViewModel extends AndroidViewModel {
    private AnimalRepository animalRepository;
    private LiveData<List<AnimalSearchResult>> animalList;
    public AnimalListViewModel(Application application) {
        super(application);
        animalRepository = new AnimalRepository(application);
        animalList = animalRepository.getAll();
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
