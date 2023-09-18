package org.moa.etlits.jobs;

import android.content.Context;
import android.util.Log;

import org.moa.etlits.utils.Constants;
import org.moa.etlits.utils.EncryptedPreferences;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LiveData;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import okhttp3.Credentials;

public class SyncWorkManager {

    public static void startSync(Context context, String syncLogId, String syncType) {
        Data.Builder inputBuilder = new Data.Builder();
        inputBuilder.putString(Constants.SYNC_LOG_ID, syncLogId);
        Data inputData = inputBuilder.build();
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        30,
                        TimeUnit.SECONDS
                )
                .addTag(syncType)
                .setInputData(inputData)
                .build();


        WorkManager.getInstance(context)
                .enqueueUniqueWork(syncType, ExistingWorkPolicy.KEEP, workRequest);
    }
}