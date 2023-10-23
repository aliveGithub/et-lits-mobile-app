package org.moa.etlits.data.dao;


import android.util.Log;

import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.models.AnimalRegistration;
import org.moa.etlits.data.models.Treatment;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public abstract class AnimalRegistrationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(AnimalRegistration animalRegistration);

    @Update
    public abstract void update(AnimalRegistration animalRegistration);

    @Query("SELECT * FROM animal_registrations")
    public abstract LiveData<List<AnimalRegistration>> getAll();

    @Query("SELECT * FROM animal_registrations")
    public abstract List<AnimalRegistration> getAllList();

    @Query("SELECT * FROM animal_registrations WHERE last_synced is null")
    public abstract List<AnimalRegistration> getAllNotSynced();

    @Query("SELECT * FROM animal_registrations WHERE id=:id")
    public abstract LiveData<AnimalRegistration> loadById(long id);

    @Transaction
    public void insertRegistration(AnimalRegistration animalRegistration,
                                   List<Animal> animalList, List<Treatment> treatmentList,
                                   AnimalDao animalDao,
                                   TreatmentDao treatmentDao) {
        long animalRegId = insert(animalRegistration);
        if (animalList != null) {
            for (Animal animal : animalList) {
                animal.setAnimalRegistrationId(animalRegId);
                animalDao.insert(animal);
            }
        }

        if (treatmentList != null) {
            for (Treatment treatment : treatmentList) {
                treatment.setAnimalRegistrationId(animalRegId);
                treatmentDao.insert(treatment);
            }
        }
    }

    @Transaction
    public void updateRegistration(AnimalRegistration animalRegistration,
                                   List<Animal> animalList, List<Treatment> treatmentList,
                                   AnimalDao animalDao,
                                   TreatmentDao treatmentDao) {
        update(animalRegistration);
        updateAnimals(animalRegistration.getId(), animalList, animalDao);
        updateTreatments(animalRegistration.getId(), treatmentList, treatmentDao);
    }

    private boolean isInNewTreatmentList(List<Treatment> newList, long treatmentId) {
        for (Treatment treatment : newList) {
            if (treatment.getId() == treatmentId) {
                return true;
            }
        }
        return false;
    }

    private boolean isInNewAnimalList(List<Animal> newList, long animalId) {
        for (Animal animal : newList) {
            if (animal.getId() == animalId) {
                return true;
            }
        }
        return false;
    }

    private void updateAnimals(long animalRegistrationId, List<Animal> newList, AnimalDao animalDao) {
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

    private void updateTreatments(long animalRegistrationId, List<Treatment> newList, TreatmentDao treatmentDao) {
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
}
