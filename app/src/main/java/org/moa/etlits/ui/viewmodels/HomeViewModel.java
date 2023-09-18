package org.moa.etlits.ui.viewmodels;

import android.app.Application;

import org.moa.etlits.data.models.SyncLog;
import org.moa.etlits.data.models.SyncLogCount;
import org.moa.etlits.data.repositories.SyncLogRepository;
import org.moa.etlits.utils.Constants;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class HomeViewModel extends ViewModel{
    private LiveData<List<SyncLogCount>> logsCountByStatus;
    private MutableLiveData<Boolean> initDialogShown = new MutableLiveData<>(false);

    private SyncLogRepository syncLogRepository;

    public HomeViewModel(Application application) {
        syncLogRepository = new SyncLogRepository(application);
        logsCountByStatus = syncLogRepository.countByStatus();
    }

    public LiveData<List<SyncLogCount>> getLogsCountByStatus() {
        return logsCountByStatus;
    }

    public Boolean getInitDialogShown() {
        return initDialogShown.getValue();
    }

    public void setInitDialogShown(Boolean initDialogShown) {
        this.initDialogShown.setValue(initDialogShown);
    }

    public void insert(SyncLog syncLog) {
        syncLogRepository.insert(syncLog);
    }
    public static class HomeViewModelFactory implements ViewModelProvider.Factory {


        private Application application;

        public HomeViewModelFactory(Application application) {
            this.application = application;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new HomeViewModel(application);
        }
    }
}
