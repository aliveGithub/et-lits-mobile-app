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

    @NonNull
    @Override
    public Result doWork() {
        ConfigService configService = RetrofitUtil.createConfigService();
        String authorization = getInputData().getString("authorization");
        Call<ConfigResponse> call = configService.getConfigData(authorization);
        syncLogRepository = new SyncLogRepository((Application) getApplicationContext());
        SyncLog syncLog = syncLogRepository.getSyncLog(Constants.SyncType.CONFIG_DATA.toString());
        if (syncLog == null) {
            syncLog = new SyncLog();
            syncLog.setType(Constants.SyncType.CONFIG_DATA.toString());
        }

        syncLog.setStatus(Constants.SyncStatus.IN_PROGRESS.toString());
        syncLog.setLastSync(new Date().getTime());
        syncLogRepository.update(syncLog);
        try {
            Response<ConfigResponse> response = call.execute();
            if (response.isSuccessful()) {
                Log.i("SyncWorker", "Success");
                ConfigResponse configResponse = response.body();
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
                                        catValue.setLanguage(value.getLanguage() != null ? value.getLanguage() :"en" );
                                        categoryValueRepository.insert(catValue);
                                    }
                                }
                        }
                    }
                }

                //TODO: persist unmovable
               /* if (configResponse.getObjectUnmovable() != null) {

                }

                if (configResponse.getObjectDetail() != null) {

                }*/



               // syncLog.setStatus(Constants.SyncStatus.COMPLETED.toString());

                //syncLogRepository.update(syncLog);

                return Result.success();
            } else {
                Log.i("SyncWorker", "Failure:" + response.code());

                return Result.failure();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }


}
