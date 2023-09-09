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

        return new SyncResult(true, 0, received, 0, "");
    }

    private SyncResult fetchAndSaveConfigData() throws IOException {
        configService = RetrofitUtil.createConfigService();
        String authorization = getInputData().getString("authorization");

        Call<ConfigResponse> call = configService.getConfigData(authorization);
        Response<ConfigResponse> response = call.execute();

        if (response.isSuccessful()) {
            Log.i("SyncWorker", "Success");
            ConfigResponse configResponse = response.body();
            return saveConfigData(configResponse);
        } else {
            return new SyncResult(response.isSuccessful(), 0, 0, 0, String.valueOf(response.code()));
        }
    }


    @NonNull
    @Override
    public Result doWork() {
        SyncLog syncLog = null;
        try {
            String syncType = getInputData().getString("syncType");
            String syncLogId = getInputData().getString("syncLogId");
            String username = getInputData().getString("username");

            syncLogRepository = new SyncLogRepository((Application) getApplicationContext());
            syncLog = syncLogRepository.getSyncLogById(syncLogId);
            syncLog.setStatus(Constants.SyncStatus.IN_PROGRESS.toString());
            syncLog.setLastSync(new Date());
            syncLog.setSyncedBy(username);
            syncLogRepository.update(syncLog);

            Thread.sleep(10000);

            if (Constants.SyncType.CONFIG_DATA.toString().equals(syncType)) {
                SyncResult configSyncResult = fetchAndSaveConfigData();
                if (configSyncResult.isSuccessful) {
                    syncLog.setStatus(Constants.SyncStatus.COMPLETED.toString());
                    syncLog.setRecordsReceived(configSyncResult.recordsReceived);
                    syncLogRepository.update(syncLog);
                    return Result.success();
                } else {
                    syncLog.setStatus(Constants.SyncStatus.FAILED.toString());
                    SyncError error = new SyncError(syncLog.getId(), configSyncResult.errorCode, "");
                    syncLogRepository.insert(error);
                    syncLogRepository.update(syncLog);
                    return Result.failure();
                }
            } else {
                syncLog.setStatus(Constants.SyncStatus.COMPLETED.toString());
                syncLogRepository.update(syncLog);
                return Result.success();
            }

        } catch (UnknownHostException e) {
            if (getRunAttemptCount() <= 3) {
                return Result.retry();
            } else {
                SyncError error = new SyncError(syncLog.getId(), String.valueOf(HttpURLConnection.HTTP_NOT_FOUND), "Server cannot be reached.");
                syncLogRepository.insert(error);
                syncLog.setStatus(Constants.SyncStatus.FAILED.toString());
                syncLogRepository.update(syncLog);
                return Result.failure();
            }
        } catch (Exception e) {
            if (getRunAttemptCount() <= 3) {
                return Result.retry();
            } else {
                SyncError error = new SyncError(syncLog.getId(), String.valueOf(Constants.UNKNOWN_SYNC_ERROR), "Unknown sync error.");
                syncLogRepository.insert(error);
                syncLog.setStatus(Constants.SyncStatus.FAILED.toString());
                syncLogRepository.update(syncLog);
                return Result.failure();
            }

        }
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
