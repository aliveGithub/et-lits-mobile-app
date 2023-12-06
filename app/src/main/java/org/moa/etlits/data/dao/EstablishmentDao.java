package org.moa.etlits.data.dao;


import org.moa.etlits.data.models.Establishment;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public abstract class EstablishmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Establishment establishment);

    @Update
    public abstract void update(Establishment establishment);

    @Transaction
    public void insertAll(List<Establishment> establishments) {
        for (Establishment establishment : establishments) {
            insert(establishment);
        }
    }

    @Query("SELECT * FROM establishments")
    public abstract LiveData<List<Establishment>> getAll();

    @Query("SELECT * FROM establishments WHERE code=:code LIMIT 1")
    public abstract LiveData<Establishment> loadByCode(String code);
}
