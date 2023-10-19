package org.moa.etlits.data.dao;


import org.moa.etlits.data.models.AnimalRegistration;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AnimalRegistrationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(AnimalRegistration animalRegistration);

    @Update
    void update(AnimalRegistration animalRegistration);

    @Query("SELECT * FROM animal_registrations")
    LiveData<List<AnimalRegistration>> getAll();

    @Query("SELECT * FROM animal_registrations")
    List<AnimalRegistration> getAllList();

    @Query("SELECT * FROM animal_registrations WHERE last_synced is null")
    List<AnimalRegistration> getAllNotSynced();

    @Query("SELECT * FROM animal_registrations WHERE id=:id")
    LiveData<AnimalRegistration> loadById(long id);
}
