package org.moa.etlits.data.repositories;

import android.app.Application;

import org.moa.etlits.data.dao.AnimalRegistrationDao;
import org.moa.etlits.data.dao.AppDatabase;
import org.moa.etlits.data.models.AnimalRegistration;

import java.util.List;

import androidx.lifecycle.LiveData;

public class AnimalRegistrationRepository {
    private AnimalRegistrationDao animalRegistrationDao;

    private LiveData<List<AnimalRegistration>> animalRegistrationList;

    public AnimalRegistrationRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        animalRegistrationDao = db.animalRegistrationDao();
        animalRegistrationList = animalRegistrationDao.getAll();
    }

    public LiveData<List<AnimalRegistration>> getAll() {
        return animalRegistrationList;
    }

    public void insert(AnimalRegistration animalRegistration) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            animalRegistrationDao.insert(animalRegistration);
        });
    }

    public void update(AnimalRegistration animalRegistration) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            animalRegistrationDao.update(animalRegistration);
        });
    }

    public LiveData<AnimalRegistration>loadById(long id){
        return animalRegistrationDao.loadById(id);
    }
}
