package org.moa.etlits.data.dao;


import org.moa.etlits.data.models.SyncError;
import org.moa.etlits.data.models.SyncLog;
import org.moa.etlits.data.models.SyncLogWithErrors;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;

@Dao
public interface SyncLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SyncLog syncLog);

    @Insert
    void insert(SyncError error);
    @Update
    void update(SyncLog syncLog);

    @Query("SELECT count(*) FROM sync_logs")
    ListenableFuture<Long> count();

    @Query("SELECT * FROM sync_logs")
    LiveData<List<SyncLog>> getAllSyncLogs();

    @Query("SELECT * FROM sync_logs where id=:logId")
    LiveData<SyncLog> loadById(String logId);

    @Query("SELECT * FROM sync_logs where type=:type")
    LiveData<SyncLog> loadByType(String type);


    @Query("SELECT * FROM sync_logs where id=:logId")
    SyncLog getSyncLogById(String logId);

    @Transaction
    @Query("SELECT * FROM sync_logs where id=:logId")
    LiveData<SyncLogWithErrors> getSyncLogWithErrors(String logId);

    @Transaction
    @Query("SELECT * FROM sync_logs ORDER BY last_sync DESC LIMIT 1")
    LiveData<SyncLogWithErrors> getLastSyncLogWithErrors();
}
