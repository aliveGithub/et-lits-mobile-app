package org.moa.etlits.jobs;

import android.content.Context;
import android.util.Log;

import org.moa.etlits.utils.Constants;
import org.moa.etlits.utils.EncryptedPreferences;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import okhttp3.Credentials;

public class SyncWorkManager {

    public static void startSync(Context context) {
        Data.Builder inputBuilder = new Data.Builder();

        String authorization = getAuthorizationHeader(context);
        if (authorization != null) {
            inputBuilder.putString("authorization", authorization);
        }

        Data inputData = inputBuilder.build();
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .setInputData(inputData)
                .build();


        WorkManager.getInstance(context)
                .enqueueUniqueWork(Constants.SyncType.CONFIG_DATA.toString(), ExistingWorkPolicy.KEEP, workRequest);


    }

    private static String getAuthorizationHeader(Context context) {
        EncryptedPreferences encryptedPreferences = new EncryptedPreferences(context);
        String username = encryptedPreferences.read(Constants.USERNAME);
        String password = encryptedPreferences.read(Constants.PASSWORD);
        if (username != null && password != null) {
            return Credentials.basic(username, password);
        }
        return null;
    }
}
