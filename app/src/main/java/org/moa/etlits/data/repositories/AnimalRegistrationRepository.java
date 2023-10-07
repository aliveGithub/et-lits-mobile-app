package org.moa.etlits.data.repositories;

import android.app.Application;
import android.util.Log;

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

    public void insert(AnimalRegistration animalRegistration, List<Animal> animalList, List<Treatment> treatmentList) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long animalRegId = animalRegistrationDao.insert(animalRegistration);
            Log.d("AnimalRegRepo", "insert: " + animalRegId);
            if (animalList != null) {
                for (Animal animal : animalList) {
                    animal.setAnimalRegistrationId(animalRegId);
                    animalDao.insert(animal);
                }
            }

            if (treatmentList != null){
                for (Treatment treatment : treatmentList) {
                    treatment.setAnimalRegistrationId(animalRegId);
                    Log.d("AnimalRegRepo", "insert: " + treatment.getTreatmentApplied());
                    treatmentDao.insert(treatment);
                }
            }
        });
    }

    public void update(AnimalRegistration animalRegistration, List<Animal> animalList, List<Treatment> treatmentList) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            animalRegistrationDao.update(animalRegistration);
            if (animalList != null) {
                for (Animal animal : animalList) {
                    if (animal.getId() == 0) {
                        animal.setAnimalRegistrationId(animalRegistration.getId());
                        db.animalDao().insert(animal);
                    }
                    db.animalDao().update(animal);
                }
            }

            if (treatmentList != null) {
                for (Treatment treatment : treatmentList) {
                    if (treatment.getId() == 0) {
                        treatment.setAnimalRegistrationId(animalRegistration.getId());
                        db.treatmentDao().insert(treatment);
                    }
                    db.treatmentDao().update(treatment);
                }
            }
        });
    }

    public LiveData<AnimalRegistration>loadById(long id){
        return animalRegistrationDao.loadById(id);
    }
}
