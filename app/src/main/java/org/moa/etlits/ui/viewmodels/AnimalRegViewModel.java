package org.moa.etlits.ui.viewmodels;

import android.app.Application;

import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.repositories.AnimalRepository;
import org.moa.etlits.utils.Constants;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class AnimalRegViewModel extends AndroidViewModel {

    private AnimalRepository animalRepository;
    private final MutableLiveData<Integer> currentStep = new MutableLiveData<>(0);

    private LiveData<Animal> animal;

    public AnimalRegViewModel(Application application) {
        super(application);
        animalRepository = new AnimalRepository(application);
    }

    public void next() {
        if (currentStep.getValue() < Constants.AnimalRegStep.values().length - 1) {
            currentStep.setValue(currentStep.getValue() + 1);
        }
    }

    public void prev() {
        if (currentStep.getValue() > 0) {
            currentStep.setValue(currentStep.getValue() - 1);
        }
    }

    public void first() {
        currentStep.setValue(0);
    }

    public void last() {
        currentStep.setValue(Constants.AnimalRegStep.values().length - 1);
    }

    public Constants.AnimalRegStep getCurrentStep() {
        return Constants.AnimalRegStep.values()[currentStep.getValue()];
    }

    public LiveData<Animal> getAnimal() {
        return animal;
    }

    public void insert(Animal animal) {
        animalRepository.insert(animal);
    }

    public void update(Animal animal) {
        animalRepository.update(animal);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private Application application;

        public Factory(Application application) {
            this.application = application;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new AnimalRegViewModel(application);
        }
    }


}
