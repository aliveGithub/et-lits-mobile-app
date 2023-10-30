package org.moa.etlits.data.repositories;

import android.app.Application;

import org.moa.etlits.data.dao.TreatmentDao;
import org.moa.etlits.data.dao.AppDatabase;
import org.moa.etlits.data.models.Treatment;

import java.util.List;

import androidx.lifecycle.LiveData;

public class TreatmentRepository {
    private TreatmentDao treatmentDao;

    public TreatmentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        treatmentDao = db.treatmentDao();
    }

    public void insert(Treatment treatment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            treatmentDao.insert(treatment);
        });
    }

    public void update(Treatment treatment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            treatmentDao.update(treatment);
        });
    }

    public void delete(long id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            treatmentDao.delete(id);
        });
    }

    public LiveData<Treatment>loadById(long treatmentId){
        return treatmentDao.loadById(treatmentId);
    }

    public LiveData<List<Treatment>> getByAnimalRegistrationId(long animalRegistrationId){
        return treatmentDao.getByAnimalRegistrationId(animalRegistrationId);
    }

    public List<Treatment> getListByAnimalRegistrationId(long animalRegistrationId){
        return treatmentDao.getListByAnimalRegistrationId(animalRegistrationId);
    }
}
