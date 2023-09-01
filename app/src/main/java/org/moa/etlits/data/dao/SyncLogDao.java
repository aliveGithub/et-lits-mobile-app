package org.moa.etlits.data.dao;


import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.models.SyncLog;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SyncLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SyncLog syncLog);

    @Update
    void update(SyncLog syncLog);

    @Query("SELECT * FROM sync_logs")
    LiveData<List<SyncLog>> getAllSyncLogs();

    @Query("SELECT * FROM sync_logs where id=:logId")
    LiveData<SyncLog> loadById(long logId);

    @Query("SELECT * FROM sync_logs where type=:type")
    LiveData<SyncLog> loadByType(String type);

    @Query("SELECT * FROM sync_logs where type=:type")
    SyncLog getSyncLog(String type);
}
