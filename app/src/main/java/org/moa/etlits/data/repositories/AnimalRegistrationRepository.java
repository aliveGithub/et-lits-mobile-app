package org.moa.etlits.data.repositories;

import android.app.Application;

import org.moa.etlits.data.dao.AnimalDao;
import org.moa.etlits.data.dao.AnimalRegistrationDao;
import org.moa.etlits.data.dao.AppDatabase;
import org.moa.etlits.data.dao.TreatmentDao;
import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.models.AnimalRegistration;
import org.moa.etlits.data.models.Treatment;

import java.util.List;

import androidx.lifecycle.LiveData;

public class AnimalRegistrationRepository {
    private AnimalRegistrationDao animalRegistrationDao;

    private AnimalDao animalDao;

    private TreatmentDao treatmentDao;
    private AppDatabase db;

    private LiveData<List<AnimalRegistration>> animalRegistrationList;

    public AnimalRegistrationRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        animalRegistrationDao = db.animalRegistrationDao();
        animalDao = db.animalDao();
        treatmentDao = db.treatmentDao();
        animalRegistrationList = animalRegistrationDao.getAll();
    }

    public LiveData<List<AnimalRegistration>> getAll() {
        return animalRegistrationList;
    }

    public List<AnimalRegistration> getAllList() {
        return animalRegistrationDao.getAllList();
    }

    public List<AnimalRegistration> getAllNotSynced(){
        return animalRegistrationDao.getAllNotSynced();
    }

    public void insert(AnimalRegistration animalRegistration, List<Animal> animalList, List<Treatment> treatmentList) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            animalRegistrationDao.insertRegistration(animalRegistration, animalList, treatmentList, animalDao, treatmentDao);
        });
    }

    public void update(AnimalRegistration animalRegistration) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            animalRegistrationDao.update(animalRegistration);
        });
    }
    public void update(AnimalRegistration animalRegistration,
                       List<Animal> animalList, List<Treatment> treatmentList) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            animalRegistrationDao.updateRegistration(animalRegistration, animalList, treatmentList, animalDao, treatmentDao);
        });
    }

    public LiveData<AnimalRegistration>loadById(long id){
        return animalRegistrationDao.loadById(id);
    }

    public LiveData<Integer> getPendingSyncCount(){
        return animalRegistrationDao.getPendingSyncCount();
    }
}
