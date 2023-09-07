package org.moa.etlits.data.repositories;

import android.app.Application;

import org.moa.etlits.data.dao.SyncLogDao;
import org.moa.etlits.data.dao.AppDatabase;
import org.moa.etlits.data.models.SyncLog;
import org.moa.etlits.data.models.SyncLog;

import java.util.List;

import androidx.lifecycle.LiveData;

public class SyncLogRepository {
    private SyncLogDao syncLogDao;

    private LiveData<List<SyncLog>> syncLogs;

    public SyncLogRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        syncLogDao = db.syncLogDao();
        syncLogs = syncLogDao.getAllSyncLogs();
    }

    public LiveData<List<SyncLog>> getAllSyncLogs() {
        return syncLogs;
    }

    public void insert(SyncLog syncLog) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            syncLogDao.insert(syncLog);
        });
    }

    public void update(SyncLog syncLog) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            syncLogDao.update(syncLog);
        });
    }

    public LiveData<SyncLog>loadById(String syncLogId){
        return syncLogDao.loadById(syncLogId);
    }

    public LiveData<SyncLog>loadByType(String type){
        return syncLogDao.loadByType(type);
    }

    public SyncLog getSyncLogById(String type) {
        return syncLogDao.getSyncLogById(type);
    }
}
