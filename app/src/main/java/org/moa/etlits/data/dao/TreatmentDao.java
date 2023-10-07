package org.moa.etlits.data.dao;


import org.moa.etlits.data.models.Treatment;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TreatmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Treatment treatment);

    @Update
    void update(Treatment treatment);

    @Query("SELECT * FROM treatments")
    LiveData<List<Treatment>> getAllTreatments();

    @Query("SELECT * FROM treatments where id=:treatmentId")
    LiveData<Treatment> loadById(long treatmentId);

    @Query("SELECT * FROM treatments where animal_registration_id=:animaRegistrationId")
    LiveData<List<Treatment>> getByAnimalRegistrationId(long animaRegistrationId);
}
