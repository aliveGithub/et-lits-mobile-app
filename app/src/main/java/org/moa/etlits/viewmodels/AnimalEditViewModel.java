package org.moa.etlits.viewmodels;

import android.app.Application;

import org.moa.etlits.models.Animal;
import org.moa.etlits.repositories.AnimalRepository;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class AnimalEditViewModel extends AndroidViewModel {

    private AnimalRepository animalRepository;

    private LiveData<Animal> animal;

    public AnimalEditViewModel(Application application, long animalId) {
        super(application);
        animalRepository = new AnimalRepository(application);
        this.animal = animalRepository.loadById(animalId);
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
        private final long animalId;
        private Application application;

        public Factory(Application application, long animalId) {
            this.animalId = animalId;
            this.application = application;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new AnimalEditViewModel(application, animalId);
        }
    }



}
