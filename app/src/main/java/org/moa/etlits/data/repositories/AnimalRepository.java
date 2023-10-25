package org.moa.etlits.data.repositories;

import android.app.Application;

import org.moa.etlits.data.dao.AnimalDao;
import org.moa.etlits.data.dao.AppDatabase;
import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.models.AnimalSearchResult;

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

    public LiveData<List<Animal>> getAll() {
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

    public void delete(long id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            animalDao.delete(id);
        });
    }
    public LiveData<Animal>loadById(long animalId){
        return animalDao.loadById(animalId);
    }

    public LiveData<List<Animal>> getByAnimalRegistrationId(long animalRegistrationId){
        return animalDao.getByAnimalRegistrationId(animalRegistrationId);
    }

    public List<Animal> getListByAnimalRegistrationId(long animalRegistrationId){
        return animalDao.getListByAnimalRegistrationId(animalRegistrationId);
    }

    public LiveData<List<AnimalSearchResult>> searchAnimals(String query){
        return animalDao.searchAnimals(query);
    }
}
