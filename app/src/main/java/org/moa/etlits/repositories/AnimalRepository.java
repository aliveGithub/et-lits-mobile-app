package org.moa.etlits.repositories;

import android.app.Application;

import org.moa.etlits.dao.AnimalDao;
import org.moa.etlits.dao.AppDatabase;
import org.moa.etlits.models.Animal;

import java.util.List;

import androidx.lifecycle.LiveData;

public class AnimalRepository {
    private AnimalDao animalDao;

    private LiveData<List<Animal>> animalList;

    public AnimalRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        animalDao = db.animalDao();
        animalList = animalDao.getAllAnimals();
    }

    public LiveData<List<Animal>> getAllAnimals() {
        return animalList;
    }

    public void insert(Animal animal) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            animalDao.insert(animal);
        });
    }

    public void update(Animal animal) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            animalDao.update(animal);
        });
    }

    public LiveData<Animal>loadById(long animalId){
        return animalDao.loadById(animalId);
    }
}
