package org.moa.etlits.jobs;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.moa.etlits.api.RetrofitUtil;
import org.moa.etlits.api.response.CatalogType;
import org.moa.etlits.api.response.ConfigResponse;
import org.moa.etlits.api.response.EntryType;
import org.moa.etlits.api.response.ValueType;
import org.moa.etlits.api.services.ConfigService;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.data.models.SyncLog;
import org.moa.etlits.data.repositories.CategoryValueRepository;
import org.moa.etlits.data.repositories.SyncLogRepository;
import org.moa.etlits.utils.Constants;

import java.io.IOException;
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

        return new SyncResult(true, 0, received, 0, 0);
    }

    private SyncResult fetchAndSaveConfigData() throws IOException {
        ConfigService configService = RetrofitUtil.createConfigService();
        String authorization = getInputData().getString("authorization");

        Call<ConfigResponse> call = configService.getConfigData(authorization);
        Response<ConfigResponse> response = call.execute();

        if (response.isSuccessful()) {
            Log.i("SyncWorker", "Success");
            ConfigResponse configResponse = response.body();
            return saveConfigData(configResponse);
        } else {
            return new SyncResult(response.isSuccessful(), 0, 0, 0, response.code());
        }
    }


    @NonNull
    @Override
    public Result doWork() {
        try {
            String syncType = getInputData().getString("syncType");
            String syncLogId = getInputData().getString("syncLogId");
            String username = getInputData().getString("username");

            syncLogRepository = new SyncLogRepository((Application) getApplicationContext());
            SyncLog syncLog = syncLogRepository.getSyncLogById(syncLogId);
            syncLog.setStatus(Constants.SyncStatus.IN_PROGRESS.toString());
            syncLog.setLastSync(new Date());
            syncLog.setSyncedBy(username);
            syncLogRepository.update(syncLog);

            Thread.sleep(10000);

            if (Constants.SyncType.CONFIG_DATA.toString().equals(syncType)) {
                SyncResult configSynResult = fetchAndSaveConfigData();
                if (configSynResult.isSuccessful) {
                    syncLog.setStatus(Constants.SyncStatus.COMPLETED.toString());
                    syncLog.setRecordsReceived(configSynResult.recordsReceived);
                    syncLogRepository.update(syncLog);
                    return Result.success();
                } else {
                    syncLog.setStatus(Constants.SyncStatus.FAILED.toString());
                    syncLogRepository.update(syncLog);
                    return Result.failure();
                }
            } else {
                syncLog.setStatus(Constants.SyncStatus.COMPLETED.toString());
                syncLogRepository.update(syncLog);
                return Result.success();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private class SyncResult {
       private int recordsSent;
        private int recordsReceived;
        private int recordsNotSent;
        private int errorCode;
        private boolean isSuccessful;


        public SyncResult(boolean isSuccessful, int recordsSent, int recordsReceived, int recordsNotSent, int errorCode) {
            this.isSuccessful = isSuccessful;
            this.recordsSent = recordsSent;
            this.recordsReceived = recordsReceived;
            this.recordsNotSent = recordsNotSent;
            this.errorCode = errorCode;
        }
    }
}
