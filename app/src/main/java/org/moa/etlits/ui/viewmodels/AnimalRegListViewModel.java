package org.moa.etlits.ui.viewmodels;

import android.app.Application;

import org.moa.etlits.data.models.AnimalRegistration;
import org.moa.etlits.data.repositories.AnimalRegistrationRepository;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
public class AnimalRegListViewModel extends AndroidViewModel {
    private AnimalRegistrationRepository animalRegistrationRepository;
    private LiveData<List<AnimalRegistration>> animalRegistrationList;
    public AnimalRegListViewModel(Application application) {
        super(application);
        animalRegistrationRepository = new AnimalRegistrationRepository(application);
        animalRegistrationList = animalRegistrationRepository.getAll();
    }
    public LiveData<List<AnimalRegistration>> getAnimalRegistrationList() {
        return animalRegistrationList;
    }
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private Application application;
        public Factory(Application application) {
            this.application = application;
        }
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new AnimalRegListViewModel(application);
        }
    }
}
