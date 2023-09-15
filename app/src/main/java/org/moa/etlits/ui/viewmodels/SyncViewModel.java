package org.moa.etlits.ui.viewmodels;

import android.app.Application;

import org.moa.etlits.data.models.SyncLog;
import org.moa.etlits.data.models.SyncLogWithErrors;
import org.moa.etlits.data.repositories.SyncLogRepository;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SyncViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> isInternetAvailable = new MutableLiveData<>(true);
    private LiveData<SyncLogWithErrors> currentSyncLog;
    private MutableLiveData<String> currentSyncId = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> syncRunning = new MutableLiveData<>(false);
    private SyncLogRepository syncLogRepository;

    private MutableLiveData<Boolean> syncStarted = new MutableLiveData<>(false);

    public SyncViewModel(Application application, String syncLogId) {
        super(application);
        syncLogRepository = new SyncLogRepository(application);
        currentSyncLog = syncLogRepository.getSyncLogWithErrors(syncLogId);
    }


    public void loadSyncLogById(String syncLogId) {
        currentSyncLog = syncLogRepository.getSyncLogWithErrors(syncLogId);
    }

    public void insert(SyncLog syncLog) {
        syncLogRepository.insert(syncLog);
        loadSyncLogById(syncLog.getId());
    }

    public void update(SyncLog syncLog) {
        syncLogRepository.update(syncLog);
    }

    public MutableLiveData<Boolean> getIsInternetAvailable() {
        return isInternetAvailable;
    }

    public void setIsInternetAvailable(MutableLiveData<Boolean> isInternetAvailable) {
        this.isInternetAvailable = isInternetAvailable;
    }

    public LiveData<SyncLogWithErrors> getCurrentSyncLog() {
        return currentSyncLog;
    }

    public MutableLiveData<String> getCurrentSyncId() {
        return currentSyncId;
    }

    public void setCurrentSyncId(String currentSyncId) {
        this.currentSyncId.setValue(currentSyncId);
    }

    public Boolean getSyncRunning() {
        return syncRunning.getValue();
    }

    public void setSyncRunning(Boolean syncRunning) {
        this.syncRunning.setValue(syncRunning);
    }

    public Boolean getSyncStarted() {
        return syncStarted.getValue();
    }

    public void setSyncStarted(Boolean syncStarted) {
        this.syncStarted.setValue(syncStarted);
    }

    public static class SyncViewModelFactory implements ViewModelProvider.Factory {
        private Application application;
        private String syncLogId;

        public SyncViewModelFactory(Application application, String syncLogId) {
            this.application = application;
            this. syncLogId = syncLogId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new SyncViewModel(application, syncLogId);
        }
    }
}
