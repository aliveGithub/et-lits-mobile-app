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

    private boolean isInNewAnimalList(List<Animal> newList, long animalId) {
        for (Animal animal : newList) {
            if (animal.getId() == animalId) {
                return true;
            }
        }
        return false;
    }

    private boolean isInNewTreatmentList(List<Treatment> newList, long treatmentId) {
        for (Treatment treatment : newList) {
            if (treatment.getId() == treatmentId) {
                return true;
            }
        }
        return false;
    }

    private void updateAnimals(long animalRegistrationId, List<Animal> newList) {
        List<Animal> oldAnimalList = animalDao.getListByAnimalRegistrationId(animalRegistrationId);
        if (oldAnimalList != null) {
            for (Animal animal : oldAnimalList) {
                if (!isInNewAnimalList(newList, animal.getId())) {
                    animalDao.delete(animal.getId());
                }
            }
        }

        if (newList != null) {
            for (Animal animal : newList) {
                if (animal.getId() == 0) {
                    animal.setAnimalRegistrationId(animalRegistrationId);
                    animalDao.insert(animal);
                } else {
                    animalDao.update(animal);
                }
            }
        }
    }

    private void updateTreatments(long animalRegistrationId, List<Treatment> newList) {
        List<Treatment> oldTreatmentList = treatmentDao.getListByAnimalRegistrationId(animalRegistrationId);
        if (oldTreatmentList != null) {
            for (Treatment treatment : oldTreatmentList) {
                if (!isInNewTreatmentList(newList, treatment.getId())) {
                    treatmentDao.delete(treatment.getId());
                }
            }
        }

        if (newList != null) {
            for (Treatment treatment : newList) {
                if (treatment.getId() == 0) {
                    treatment.setAnimalRegistrationId(animalRegistrationId);
                    treatmentDao.insert(treatment);
                } else {
                    treatmentDao.update(treatment);
                }
            }
        }
    }

    public void update(AnimalRegistration animalRegistration,
                       List<Animal> animalList, List<Treatment> treatmentList) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            animalRegistrationDao.update(animalRegistration);
            updateAnimals(animalRegistration.getId(), animalList);
            updateTreatments(animalRegistration.getId(), treatmentList);
        });
    }

    public LiveData<AnimalRegistration>loadById(long id){
        return animalRegistrationDao.loadById(id);
    }
}
