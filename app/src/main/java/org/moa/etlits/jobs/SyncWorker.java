package org.moa.etlits.jobs;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.moa.etlits.api.RetrofitUtil;
import org.moa.etlits.api.response.CatalogType;
import org.moa.etlits.api.response.ConfigResponse;
import org.moa.etlits.api.response.EntryType;
import org.moa.etlits.api.response.TypeObjectUnmovable;
import org.moa.etlits.api.response.ValueType;
import org.moa.etlits.api.services.ConfigService;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.data.models.SyncError;
import org.moa.etlits.data.models.SyncLog;
import org.moa.etlits.data.repositories.CategoryValueRepository;
import org.moa.etlits.data.repositories.EstablishmentRepository;
import org.moa.etlits.data.repositories.SyncLogRepository;
import org.moa.etlits.utils.Constants;
import org.moa.etlits.utils.EncryptedPreferences;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Response;

public class SyncWorker extends Worker {

    private final ConfigService configService;
    private final CategoryValueRepository categoryValueRepository;
    private SyncLogRepository syncLogRepository;

    private EstablishmentRepository establishmentRepository;

    private final EncryptedPreferences encryptedPreferences;
    private final SharedPreferences sharedPreferences;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        encryptedPreferences = new EncryptedPreferences(context);
        configService = RetrofitUtil.createConfigService();
        categoryValueRepository = new CategoryValueRepository((Application) getApplicationContext());
        establishmentRepository = new EstablishmentRepository((Application) getApplicationContext());
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    private SyncResult saveConfigData(ConfigResponse configResponse) {
        int received = 0;
        if (configResponse != null) {

            if (configResponse.getCatalogs() != null) {
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

            if (configResponse.getObjectUnmovable() != null) {
                for (TypeObjectUnmovable unmovable : configResponse.getObjectUnmovable()) {
                    establishmentRepository.insert(unmovable);
                    ++received;
                }
            }
        }
        //TODO: save - objectUnmovable
        //TODO: save - objectDetail
        return new SyncResult(true, 0, received, 0, "");
    }

     private SyncResult fetchAndSaveConfigData() throws IOException, SyncStoppedException {
        String username = encryptedPreferences.read(Constants.USERNAME);
        String password = encryptedPreferences.read(Constants.PASSWORD);
        String authorization = Credentials.basic(username, password);

        Call<ConfigResponse> call = configService.getConfigData(authorization);
        Response<ConfigResponse> response = call.execute();
        checkSyncStatus();
        if (response.isSuccessful()) {
            ConfigResponse configResponse = response.body();
            return saveConfigData(configResponse);
        } else {
            return new SyncResult(false, 0, 0, 0, String.valueOf(response.code()));
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
            SyncResult configSyncResult = fetchAndSaveConfigData();
            if (configSyncResult.isSuccessful) {
                updateSyncStatus(Constants.SyncStatus.SUCCESSFUL.toString(), configSyncResult);

                if (!sharedPreferences.getBoolean(Constants.HAS_INITIALIZED, false)) {
                    sharedPreferences.edit().putBoolean(Constants.HAS_INITIALIZED, true).apply();
                }

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
                logError(Constants.SERVER_UNREACHABLE, "Server cannot be reached.");
                return Result.retry();
            } else {
                logError(Constants.SERVER_UNREACHABLE, "Server cannot be reached.");
                return Result.failure();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (getRunAttemptCount() < 3) {
                logError(Constants.UNKNOWN_SYNC_ERROR, e.toString());
                return Result.retry();
            } else {
                logError(Constants.UNKNOWN_SYNC_ERROR, e.toString());
                return Result.failure();
            }
        }
    }

    private void logError(String errorCode, String errorMessage) {
        String syncLogId = getInputData().getString(Constants.SYNC_LOG_ID);
        SyncError error = new SyncError(syncLogId, errorCode, errorMessage);
        syncLogRepository.insert(error);
        updateSyncStatus(Constants.SyncStatus.FAILED.toString(), null);
    }

    private void updateSyncStatus(String status, SyncResult syncResult) {
        String syncLogId = getInputData().getString(Constants.SYNC_LOG_ID);
        SyncLog syncLog = syncLogRepository.getSyncLogById(syncLogId);
        if (status.equals(Constants.SyncStatus.IN_PROGRESS.toString())) {
            syncLog.setLastSync(new Date());
        }

        if (syncResult != null) {
            syncLog.setRecordsSent(syncResult.recordsSent);
            syncLog.setRecordsReceived(syncResult.recordsReceived);
            syncLog.setRecordsNotSent(syncResult.recordsNotSent);
        }

        syncLog.setStatus(status);
        syncLogRepository.update(syncLog);

        if (!sharedPreferences.getBoolean(Constants.INITIAL_SYNC_STARTED, false)) {
            sharedPreferences.edit().putBoolean(Constants.INITIAL_SYNC_STARTED, true).apply();
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
