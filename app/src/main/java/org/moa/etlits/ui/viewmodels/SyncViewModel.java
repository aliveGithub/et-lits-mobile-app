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
    private LiveData<SyncLogWithErrors> syncLog; // last sync or current
    private MutableLiveData<String> currentSyncId = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> syncRunning = new MutableLiveData<>(false);

    private SyncLogRepository syncLogRepository;

    public SyncViewModel(Application application) {
        super(application);
        syncLogRepository = new SyncLogRepository(application);
        syncLog = syncLogRepository.getLastSyncLog();
    }


    public void loadSyncLogById(String syncLogId) {
        syncLog = syncLogRepository.getSyncLogWithErrors(syncLogId);
    }

    public void insert(SyncLog syncLog) {
        syncLogRepository.insert(syncLog);
        loadSyncLogById(syncLog.getId());
    }

    public void update(SyncLog syncLog) {
        syncLogRepository.update(syncLog);
    }


    public LiveData<SyncLogWithErrors> getSyncLog() {
        return syncLog;
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



    public static class SyncViewModelFactory implements ViewModelProvider.Factory {
        private Application application;

        public SyncViewModelFactory(Application application) {
            this.application = application;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new SyncViewModel(application);
        }
    }
}
