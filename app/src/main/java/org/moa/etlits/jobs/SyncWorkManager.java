package org.moa.etlits.jobs;

import android.content.Context;
import android.util.Log;

import org.moa.etlits.utils.Constants;
import org.moa.etlits.utils.EncryptedPreferences;

import java.util.List;

import androidx.lifecycle.LiveData;
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

        EncryptedPreferences encryptedPreferences = new EncryptedPreferences(context);
        String username = encryptedPreferences.read(Constants.USERNAME);
        String password = encryptedPreferences.read(Constants.PASSWORD);
        String authorization = null;
        if (username != null && password != null) {
            authorization = Credentials.basic(username, password);
        }
        if (authorization != null) {
            inputBuilder.putString("authorization", authorization);
        }
        inputBuilder.putString("syncLogId", syncLogId);
        inputBuilder.putString("syncType", syncType);
        inputBuilder.putString("username", username);

        Data inputData = inputBuilder.build();
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .addTag(syncType)
                .setInputData(inputData)
                .build();


        WorkManager.getInstance(context)
                .enqueueUniqueWork(syncType, ExistingWorkPolicy.KEEP, workRequest);



    }

}
