package org.moa.etlits.ui.viewmodels;

import android.app.Application;

import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.repositories.AnimalRepository;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


public class AnimalViewModel extends AndroidViewModel {
    private AnimalRepository animalRepository;
    private final LiveData<List<Animal>> allAnimals;

    public AnimalViewModel(Application application) {
        super(application);
        animalRepository = new AnimalRepository(application);
        allAnimals = animalRepository.getAllAnimals();
    }

    public LiveData<List<Animal>> getAllAnimals() {
        return allAnimals;
    }

    public void insert(Animal animal) {
        animalRepository.insert(animal);
    }

    public void update(long id, String tag, String method, Date dateIdentification) {
         /*Animal animal = new Animal(id, tag, method, dateIdentification);
         animalRepository.update(animal);*/
    }
}
