package org.moa.etlits.ui.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.SyncError;
import org.moa.etlits.data.models.SyncLog;
import org.moa.etlits.data.models.SyncLogWithErrors;
import org.moa.etlits.jobs.SyncWorkManager;
import org.moa.etlits.ui.viewmodels.SyncViewModel;
import org.moa.etlits.utils.Constants;
import org.moa.etlits.utils.InternetConnectionChecker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class SyncActivity extends AppCompatActivity {
    private TextView statusTextView;

    private TextView lastSync;

    private TextView internetStatus;

    private TextView recordsToSend;

    private TextView syncedBy;

    private SyncViewModel syncViewModel;

    private Button startSync;

    private ProgressBar loadingSpinner;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    private InternetConnectionChecker internetConnectionCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_data_sync);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        startSync = findViewById(R.id.btn_sync);
        statusTextView = findViewById(R.id.tv_status);
        lastSync = findViewById(R.id.tv_last_sync);
        internetStatus = findViewById(R.id.tv_network_status);
        recordsToSend = findViewById(R.id.tv_records_count);
        syncedBy = findViewById(R.id.tv_last_sync_user);
        loadingSpinner = findViewById(R.id.pb_loading);


        internetConnectionCheck = new InternetConnectionChecker((ConnectivityManager)getApplication().getSystemService(Context.CONNECTIVITY_SERVICE));
        internetConnectionCheck.observe(this, isConnected -> {
            if (isConnected) {
                internetStatus.setVisibility(View.INVISIBLE);
            } else {
                internetStatus.setVisibility(View.VISIBLE);
            }
        });

        String syncLogId = getIntent().getStringExtra("syncLogId");
        String syncType = getIntent().getStringExtra("syncType") != null
                ? getIntent().getStringExtra("syncType")
                : Constants.SyncType.ALL_DATA.toString();

        syncViewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new SyncViewModel.SyncViewModelFactory(getApplication(), syncLogId)).get(SyncViewModel.class);
        if (syncLogId != null) {
            syncViewModel.setCurrentSyncId(syncLogId);
            syncViewModel.getCurrentSyncLog().observe(this, syncLog -> {
                 updateUI(syncLog);
            });
        }

        updateUI(null);

        startSync.setOnClickListener(v -> {
            if (!syncViewModel.getSyncRunning()) {
                if (syncLogId == null && syncViewModel.getCurrentSyncId().getValue() == null) {
                    String newSyncLogId = UUID.randomUUID().toString();
                    SyncLog newSyncLog = new SyncLog(newSyncLogId,
                            new Date(),
                            syncType,
                            Constants.SyncStatus.IN_PROGRESS.toString());

                    syncViewModel.setCurrentSyncId(newSyncLogId);
                    syncViewModel.insert(newSyncLog);

                    syncViewModel.getCurrentSyncLog().observe(this, syncLog -> {
                        updateUI(syncLog);
                    });
                }

                SyncWorkManager.startSync(this, syncViewModel.getCurrentSyncId().getValue(), syncType);
                WorkManager.getInstance(this).getWorkInfosByTagLiveData(syncType).observe(this, workInfos -> {
                    if (hasRunningWork(workInfos)) {
                        syncViewModel.setSyncRunning(true);
                        loadingSpinner.setVisibility(View.VISIBLE);
                        GradientDrawable border = new GradientDrawable();
                        border.setColor(ContextCompat.getColor(this, R.color.white));
                        border.setStroke(5, ContextCompat.getColor(this, R.color.colorPrimary));
                        //border.setCornerRadius(30);
                        startSync.setBackground(border);
                        startSync.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                        startSync.setText(R.string.btn_sync_stop);
                    } else {
                        syncViewModel.setSyncRunning(false);
                        loadingSpinner.setVisibility(View.GONE);
                        startSync.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                        startSync.setTextColor(ContextCompat.getColor(this, R.color.white));
                        startSync.setText(R.string.btn_sync_start);
                    }
                });

            } else {
                WorkManager.getInstance(this).cancelAllWorkByTag(syncType);
            }
        });
    }

    private boolean hasRunningWork(List<WorkInfo> workInfos) {
        for (WorkInfo w : workInfos) {
            if (!w.getState().isFinished()) {
                return true;
            }
        }

        return false;
    }

    private void updateUI(SyncLogWithErrors log) {
        Log.i("Sync", "Updating UI ....................");
        if (log != null && log.syncLog != null) {
            syncedBy.setVisibility(View.VISIBLE);
            lastSync.setVisibility(View.VISIBLE);

            statusTextView.setText(log.syncLog.getStatus());
            if (log.syncLog.getLastSync() != null) {
                lastSync.setText(getString(R.string.sync_date, dateFormat.format(log.syncLog.getLastSync())));
            }
            recordsToSend.setText(String.valueOf(log.syncLog.getRecordsToSend()));
            syncedBy.setText(getString(R.string.synced_by, log.syncLog.getSyncedBy() != null ? log.syncLog.getSyncedBy() : ""));
            if (log.errors != null) {
                for (SyncError error : log.errors) {
                    Log.i("SyncError", error.getErrorKey());
                }

            }
        } else {
            Log.i("Sync", "SyncLog is null ....................");
            syncedBy.setVisibility(View.GONE);
            lastSync.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sync_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (syncViewModel.getSyncRunning()) {
                showDataInitDialog();
            } else {
                finish();
            }
            return true;
        } else if (item.getItemId() == R.id.action_info) {
            startActivity(new Intent(this, SyncInfoActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDataInitDialog() {
        final Dialog customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.custom_dialog);
        customDialog.setCancelable(false);

        TextView title = customDialog.findViewById(R.id.dialog_title);
        Button positiveButton = customDialog.findViewById(R.id.positive_button);
        TextView message = customDialog.findViewById(R.id.dialog_message);
        Button negativeButton = customDialog.findViewById(R.id.negative_button);
        Button neutralButton = customDialog.findViewById(R.id.neutral_button);

        title.setText(R.string.sync_init_dialog_title);
        message.setText(R.string.sync_init_dialog_exit_msg);

        neutralButton.setVisibility(View.GONE);

        negativeButton.setText(R.string.sync_init_dialog_exit_yes);
        negativeButton.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });

        positiveButton.setText(R.string.sync_init_dialog_exit_no);
        positiveButton.setOnClickListener(v -> {
            Intent intent = new Intent(SyncActivity.this, SyncActivity.class);
            intent.putExtra("syncType", Constants.SyncType.CONFIG_DATA.toString());
            startActivity(intent);
            customDialog.dismiss();
        });

        customDialog.show();
    }
}