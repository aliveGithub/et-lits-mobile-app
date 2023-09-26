package org.moa.etlits.data.dao;


import org.moa.etlits.data.models.Establishment;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface EstablishmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Establishment establishment);

    @Update
    void update(Establishment establishment);

    @Query("SELECT * FROM establishments")
    LiveData<List<Establishment>> getAll();

    @Query("SELECT * FROM establishments WHERE code=:code LIMIT 1")
    LiveData<Establishment> loadByCode(String code);
}
