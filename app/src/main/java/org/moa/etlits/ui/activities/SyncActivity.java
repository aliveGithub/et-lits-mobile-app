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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.SyncLog;
import org.moa.etlits.data.models.SyncLogWithErrors;
import org.moa.etlits.jobs.SyncWorkManager;
import org.moa.etlits.ui.adapters.SyncErrorAdapter;
import org.moa.etlits.ui.viewmodels.SyncViewModel;
import org.moa.etlits.utils.Constants;
import org.moa.etlits.utils.InternetConnectionChecker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private TextView stoppedByUser;

    private SyncViewModel syncViewModel;

    private Button startSync;

    private ProgressBar loadingSpinner;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    private InternetConnectionChecker internetConnectionCheck;

    private TextView recordsSent;

    private TextView recordsNotSent;

    private TextView recordsReceived;

    private TextView staleData;

    private ListView errorsList;

    private SyncErrorAdapter syncErrorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_new);

        String syncLogId = getIntent().getStringExtra("syncLogId");
        String syncType = getIntent().getStringExtra("syncType") != null
                ? getIntent().getStringExtra("syncType")
                : Constants.SyncType.ALL_DATA.toString();

        initViews();
        initViewModels(syncLogId, syncType);
        addEventListeners(syncLogId, syncType);

        if (Constants.SyncType.CONFIG_DATA.toString().equals(syncType)
        && savedInstanceState == null) {
            startSync(syncLogId, syncType);
            trackWorkStatus(syncType);
        }
    }

    private void initViews() {
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
        stoppedByUser = findViewById(R.id.tv_stopped_by_user);
        loadingSpinner = findViewById(R.id.pb_loading);
        recordsSent = findViewById(R.id.tv_records_sent);
        recordsNotSent = findViewById(R.id.tv_records_not_sent);
        recordsReceived = findViewById(R.id.tv_records_received);
        staleData = findViewById(R.id.tv_stale_data);
        errorsList = findViewById(R.id.lst_errors);

        syncErrorAdapter = new SyncErrorAdapter(this, new ArrayList<>());
        errorsList.setAdapter(syncErrorAdapter);

        internetConnectionCheck = new InternetConnectionChecker((ConnectivityManager)getApplication().getSystemService(Context.CONNECTIVITY_SERVICE));
        internetConnectionCheck.observe(this, isConnected -> {
            if (isConnected) {
                internetStatus.setVisibility(View.INVISIBLE);
            } else {
                internetStatus.setVisibility(View.VISIBLE);
            }
        });

        updateUI(null);
    }



    private void initViewModels(String syncLogId, String syncType) {
        syncViewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new SyncViewModel.SyncViewModelFactory(getApplication(), syncLogId)).get(SyncViewModel.class);
        if (syncLogId != null) {
            syncViewModel.setCurrentSyncId(syncLogId);
            syncViewModel.getCurrentSyncLog().observe(this, syncLog -> {
                updateUI(syncLog);
            });
        }
    }

    private void addEventListeners(String syncLogId, String syncType) {
        startSync.setOnClickListener(v -> {
            if (!syncViewModel.getSyncRunning()) {
                startSync(syncLogId, syncType);
                trackWorkStatus(syncType);
            } else {
                if (syncViewModel.getCurrentSyncLog().getValue() != null
                        && syncViewModel.getCurrentSyncLog().getValue().syncLog != null) {
                    syncViewModel.getCurrentSyncLog().getValue().syncLog.setStatus(Constants.SyncStatus.STOPPING.toString());
                    syncViewModel.update(syncViewModel.getCurrentSyncLog().getValue().syncLog);
                }

                WorkManager.getInstance(this).cancelAllWorkByTag(syncType);
            }
        });
    }

    private void startSync(String syncLogId, String syncType) {
        if (syncLogId == null && syncViewModel.getCurrentSyncId().getValue() == null) {
            String newSyncLogId = UUID.randomUUID().toString();
            SyncLog newSyncLog = new SyncLog(newSyncLogId,
                    new Date(),
                    syncType,
                    Constants.SyncStatus.INITIALIZING.toString());

            syncViewModel.setCurrentSyncId(newSyncLogId);
            syncViewModel.insert(newSyncLog);

            syncViewModel.getCurrentSyncLog().observe(this, syncLog -> {
                updateUI(syncLog);
            });
        }

        SyncWorkManager.startSync(this, syncViewModel.getCurrentSyncId().getValue(), syncType);
    }

    private void trackWorkStatus(String syncType) {
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
                loadingSpinner.setVisibility(View.INVISIBLE);
                startSync.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                startSync.setTextColor(ContextCompat.getColor(this, R.color.white));
                startSync.setText(R.string.btn_sync_start);
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
        if (log != null && log.syncLog != null) {
            lastSync.setVisibility(View.VISIBLE);
            statusTextView.setText(getStatusMessage(log.syncLog.getStatus()));
            if (log.syncLog.getLastSync() != null) {
                lastSync.setText(getString(R.string.sync_date, dateFormat.format(log.syncLog.getLastSync())));
            }
            recordsToSend.setText(String.valueOf(log.syncLog.getRecordsToSend()));
            recordsSent.setText(getString(R.string.sync_records_sent, String.valueOf(log.syncLog.getRecordsSent())));
            recordsNotSent.setText(getString(R.string.sync_records_not_sent, String.valueOf(log.syncLog.getRecordsNotSent())));
            recordsReceived.setText(getString(R.string.sync_records_received, String.valueOf(log.syncLog.getRecordsReceived())));

            if (Constants.SyncStatus.STOPPED.toString().equals(log.syncLog.getStatus())) {
                stoppedByUser.setVisibility(View.VISIBLE);
            } else {
                stoppedByUser.setVisibility(View.GONE);
            }

            syncErrorAdapter.clear();
            if (log.errors != null) {
                Log.d("Sync", "Errors...................." + log.errors.size());
                syncErrorAdapter.addAll(log.errors);
                syncErrorAdapter.notifyDataSetChanged();
            }
        } else {
            stoppedByUser.setVisibility(View.GONE);
            lastSync.setVisibility(View.INVISIBLE);
            recordsSent.setText(getString(R.string.sync_records_sent, String.valueOf(0)));
            recordsNotSent.setText(getString(R.string.sync_records_not_sent, String.valueOf(0)));
            recordsReceived.setText(getString(R.string.sync_records_received, String.valueOf(0)));
        }
    }

    private String getStatusMessage(String status) {
        if(Constants.SyncStatus.INITIALIZING.toString().equals(status)) {
               return getString(R.string.sync_status_initializing);
        } else if (Constants.SyncStatus.IN_PROGRESS.toString().equals(status)) {
            return getString(R.string.sync_status_in_progress);
        } else if (Constants.SyncStatus.COMPLETED.toString().equals(status)) {
            return getString(R.string.sync_status_completed);
        } else if (Constants.SyncStatus.FAILED.toString().equals(status)) {
            return getString(R.string.sync_status_failed);
        } else if (Constants.SyncStatus.STOPPING.toString().equals(status)) {
            return getString(R.string.sync_status_stopping);
        } else if (Constants.SyncStatus.STOPPED.toString().equals(status)) {
            return getString(R.string.sync_status_stopped);
        } else {
            return getString(R.string.sync_status_not_started);
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
            SyncLogWithErrors log = syncViewModel.getCurrentSyncLog().getValue();
            if (log !=null && log.syncLog != null && Constants.SyncType.CONFIG_DATA.toString().equals(log.syncLog.getType())
                    &&  !Constants.SyncStatus.COMPLETED.toString().equals(log.syncLog.getStatus())) {
                showDataInitDialog();
            } else {
                SyncActivity.super.onBackPressed();
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
            customDialog.dismiss();
        });

        customDialog.show();
    }
}