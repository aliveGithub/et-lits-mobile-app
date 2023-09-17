package org.moa.etlits.jobs;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.moa.etlits.api.RetrofitUtil;
import org.moa.etlits.api.response.CatalogType;
import org.moa.etlits.api.response.ConfigResponse;
import org.moa.etlits.api.response.EntryType;
import org.moa.etlits.api.response.ValueType;
import org.moa.etlits.api.services.ConfigService;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.data.models.SyncError;
import org.moa.etlits.data.models.SyncLog;
import org.moa.etlits.data.repositories.CategoryValueRepository;
import org.moa.etlits.data.repositories.SyncLogRepository;
import org.moa.etlits.utils.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Call;
import retrofit2.Response;

public class SyncWorker extends Worker {

    private ConfigService configService;
    private CategoryValueRepository categoryValueRepository;
    private SyncLogRepository syncLogRepository;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private SyncResult saveConfigData(ConfigResponse configResponse) {
        int received = 0;
        if (configResponse != null) {
            if (configResponse.getCatalogs() != null) {
                categoryValueRepository = new CategoryValueRepository((Application) getApplicationContext());
                for (CatalogType catalog : configResponse.getCatalogs()) {
                    for (EntryType entry : catalog.getEntry()) {
                        CategoryValue catValue = new CategoryValue();
                        catValue.setCategoryKey(catalog.getKey());
                        catValue.setValueId(entry.getId());
                        for (ValueType value : entry.getValue()) {
                            catValue.setValue(value.getValue());
                            catValue.setLanguage(value.getLanguage() != null ? value.getLanguage() : "en");
                            categoryValueRepository.insert(catValue);
                            ++received;
                        }
                    }
                }
            }
        }
        //TODO: save - objectUnmovable
        //TODO: save - objectDetail
        return new SyncResult(true, 0, received, 0, "");
    }

    private SyncResult fetchAndSaveConfigData() throws IOException, SyncStoppedException {
        String authorization = getInputData().getString("authorization");
        configService = RetrofitUtil.createConfigService();
        Call<ConfigResponse> call = configService.getConfigData(authorization);
        Response<ConfigResponse> response = call.execute();
        checkSyncStatus();
        if (response.isSuccessful()) {
            Log.d("SyncWorker", "Success");
            ConfigResponse configResponse = response.body();
            return saveConfigData(configResponse);
        } else {
            return new SyncResult(!response.isSuccessful(), 0, 0, 0, String.valueOf(response.code()));
        }
    }

    private void checkSyncStatus() throws SyncStoppedException {
        if (isStopped()) {
            throw new SyncStoppedException();
        }
    }

    @NonNull
    @Override
    public Result doWork() {

        try {
            syncLogRepository = new SyncLogRepository((Application) getApplicationContext());
            updateSyncStatus(Constants.SyncStatus.IN_PROGRESS.toString(), null);

             Thread.sleep(5000);

            SyncResult configSyncResult = fetchAndSaveConfigData();
            if (configSyncResult.isSuccessful) {
                updateSyncStatus(Constants.SyncStatus.COMPLETED.toString(), configSyncResult);
                return Result.success();
            } else {
                logError(configSyncResult.errorCode, "");
                return Result.failure();
            }
        } catch (SyncStoppedException e) {
            updateSyncStatus(Constants.SyncStatus.STOPPED.toString(), null);
            return Result.failure();
        } catch (UnknownHostException e) {
            if (getRunAttemptCount() < 3) {
                return Result.retry();
            } else {
                logError(String.valueOf(HttpURLConnection.HTTP_NOT_FOUND), "Server cannot be reached.");
                return Result.failure();
            }
        } catch (Exception e) {
            if (getRunAttemptCount() < 3) {
                return Result.retry();
            } else {
                logError(Constants.UNKNOWN_SYNC_ERROR, "Unknown sync error.");
                return Result.failure();
            }
        }
    }

    private void logError(String errorCode, String errorMessage){
        String syncLogId = getInputData().getString("syncLogId");
        SyncError error = new SyncError(syncLogId, errorCode, errorMessage);
        syncLogRepository.insert(error);
        updateSyncStatus(Constants.SyncStatus.FAILED.toString(), null);
    }

    private void updateSyncStatus(String status, SyncResult syncResult){
        String syncLogId = getInputData().getString("syncLogId");
        SyncLog syncLog = syncLogRepository.getSyncLogById(syncLogId);
        String username = getInputData().getString("username");
        if (status.equals(Constants.SyncStatus.IN_PROGRESS.toString())){
            syncLog.setLastSync(new Date());
            syncLog.setSyncedBy(username);
        }
        if (syncResult != null) {
            syncLog.setRecordsSent(syncResult.recordsSent);
            syncLog.setRecordsReceived(syncResult.recordsReceived);
            syncLog.setRecordsNotSent(syncResult.recordsNotSent);

            //add dummy errors
       syncLogRepository.insert(new SyncError(syncLogId, "404", ""));
        syncLogRepository.insert(new SyncError(syncLogId, "405", ""));
        syncLogRepository.insert(new SyncError(syncLogId, "406", ""));
        syncLogRepository.insert(new SyncError(syncLogId, "407", ""));
        syncLogRepository.insert(new SyncError(syncLogId, "408", ""));
        syncLogRepository.insert(new SyncError(syncLogId, "409", ""));
        syncLogRepository.insert(new SyncError(syncLogId, "410", ""));
        syncLogRepository.insert(new SyncError(syncLogId, "412", ""));
        syncLogRepository.insert(new SyncError(syncLogId, "413", ""));
        syncLogRepository.insert(new SyncError(syncLogId, "414", ""));
        }



        syncLog.setStatus(status);
        syncLogRepository.update(syncLog);
    }

    private class SyncResult {
        private int recordsSent;
        private int recordsReceived;
        private int recordsNotSent;
        private String errorCode;
        private boolean isSuccessful;


        public SyncResult(boolean isSuccessful, int recordsSent, int recordsReceived, int recordsNotSent, String errorCode) {
            this.isSuccessful = isSuccessful;
            this.recordsSent = recordsSent;
            this.recordsReceived = recordsReceived;
            this.recordsNotSent = recordsNotSent;
            this.errorCode = errorCode;
        }
    }
}
