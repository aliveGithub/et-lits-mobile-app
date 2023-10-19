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

    public List<AnimalRegistration> getAllList() {
        return animalRegistrationDao.getAllList();
    }

    public List<AnimalRegistration> getAllNotSynced(){
        return animalRegistrationDao.getAllNotSynced();
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

    public void update(AnimalRegistration animalRegistration) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            animalRegistrationDao.update(animalRegistration);
        });
    }

    public void update(AnimalRegistration animalRegistration,
                       List<Animal> animalList, List<Treatment> treatmentList,
                       List<Long> idsForRemovedAnimals, List<Long> idsForRemovedTreatments) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            animalRegistrationDao.update(animalRegistration);
            if (animalList != null) {
                for (Animal animal : animalList) {
                    if (animal.getId() == 0) {
                        animal.setAnimalRegistrationId(animalRegistration.getId());
                        animalDao.insert(animal);
                    } else {
                        animalDao.update(animal);
                    }
                }
            }
            for (Long id : idsForRemovedAnimals) {
                animalDao.deleteById(id);
            }

            if (treatmentList != null) {
                for (Treatment treatment : treatmentList) {
                    if (treatment.getId() == 0) {
                        treatment.setAnimalRegistrationId(animalRegistration.getId());
                        treatmentDao.insert(treatment);
                    } else {
                        treatmentDao.update(treatment);
                    }

                }
            }
            for (Long id : idsForRemovedTreatments) {
                treatmentDao.deleteById(id);
            }
        });
    }

    public LiveData<AnimalRegistration>loadById(long id){
        return animalRegistrationDao.loadById(id);
    }
}
