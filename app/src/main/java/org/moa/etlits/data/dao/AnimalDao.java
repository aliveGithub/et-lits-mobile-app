package org.moa.etlits.data.dao;


import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.models.AnimalSearchResult;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public interface AnimalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Animal animal);

    @Update
    void update(Animal animal);

    @Query("DELETE FROM animals where id=:id")
    void delete(long id);

    @Query("SELECT * FROM animals")
    LiveData<List<Animal>> getAllAnimals();

    @Query("SELECT * FROM animals where id=:animalId")
    LiveData<Animal> loadById(long animalId);

    @Query("SELECT * FROM animals where animal_registration_id=:animalRegistrationId")
    LiveData<List<Animal>> getByAnimalRegistrationId(long animalRegistrationId);

    @Query("SELECT * FROM animals where animal_registration_id=:animalRegistrationId")
    List<Animal> getListByAnimalRegistrationId(long animalRegistrationId);

    @Transaction
    @Query("SELECT animals.id, animals.animal_id as animalId, establishment_eid as eid FROM animals " +
            "JOIN animal_registrations ON animals.animal_registration_id = animal_registrations.id " +
            "WHERE animals.animal_id LIKE :query OR animal_registrations.establishment_eid LIKE :query " +
            "ORDER BY animals.animal_id " +
            "LIMIT 10")
    LiveData<List<AnimalSearchResult>> searchAnimals(String query);



    @Transaction
    @Query("SELECT animals.id, animals.animal_id as animalId, animals.sex, animals.age, animals.dead, date_move_on as eventDate, establishment_eid as eid, animals.breed FROM animals " +
            "JOIN animal_registrations ON animals.animal_registration_id = animal_registrations.id " +
            "ORDER BY animals.animal_id ")
    LiveData<List<AnimalSearchResult>> getAll();

    @Transaction
    @Query("SELECT animals.id, animals.animal_id as animalId, animals.sex, animals.age, animals.dead, date_move_on as eventDate, ar.establishment_eid as eid, animals.breed,est.name as establishmentName FROM animals " +
            "JOIN animal_registrations ar ON animals.animal_registration_id = ar.id " +
            "JOIN establishments est ON est.code = ar.establishment_eid " +
            "WHERE animals.animal_id=:animalId")
    LiveData<AnimalSearchResult> loadByAnimalId(String animalId);
}
