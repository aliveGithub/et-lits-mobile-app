package org.moa.etlits.ui.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import org.moa.etlits.data.models.SyncLogCount;
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
        Boolean startSync = getIntent().getBooleanExtra("startSync", false);

        initViews();
        initViewModels();
        addEventListeners();

        if (startSync  && savedInstanceState == null) {
            startSync();
            trackWorkStatus();
        }
    }

    private void initViews() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_data_sync);
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            if (upArrow != null) {
                upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark, getTheme()), PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
            }
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
        updateSyncButton(false);
    }



    private void initViewModels() {
        syncViewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new SyncViewModel.SyncViewModelFactory(getApplication())).get(SyncViewModel.class);
        syncViewModel.getSyncLog().observe(this, syncLog -> {
                updateUI(syncLog);
        });
    }

    private void addEventListeners() {
        startSync.setOnClickListener(v -> {
            if (!syncViewModel.getSyncRunning()) {
                startSync();
                trackWorkStatus();
            } else {
                if (syncViewModel.getSyncLog().getValue() != null
                        && syncViewModel.getSyncLog().getValue().syncLog != null) {
                    syncViewModel.getSyncLog().getValue().syncLog.setStatus(Constants.SyncStatus.STOPPING.toString());
                    syncViewModel.update(syncViewModel.getSyncLog().getValue().syncLog);
                }

                WorkManager.getInstance(this).cancelAllWorkByTag(Constants.SyncType.ALL_DATA.toString());
            }
        });
    }

    private void startSync() {
         String newSyncLogId = UUID.randomUUID().toString();
            SyncLog newSyncLog = new SyncLog(newSyncLogId,
                    new Date(),
                    Constants.SyncType.ALL_DATA.toString(),
                    Constants.SyncStatus.INITIALIZING.toString());

            syncViewModel.setCurrentSyncId(newSyncLogId);
            syncViewModel.insert(newSyncLog);

            syncViewModel.getSyncLog().observe(this, syncLog -> {
                updateUI(syncLog);
            });


        SyncWorkManager.startSync(this, syncViewModel.getCurrentSyncId().getValue(), Constants.SyncType.ALL_DATA.toString());
    }

    private void updateSyncButton(boolean isRunning) {
        if (isRunning) {
            GradientDrawable border = new GradientDrawable();
            border.setColor(ContextCompat.getColor(this, R.color.white));
            border.setStroke(5, ContextCompat.getColor(this, R.color.colorPrimary));
            border.setCornerRadius(50);
            startSync.setBackground(border);

            startSync.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            startSync.setText(R.string.btn_sync_stop);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_stop_bold);
            startSync.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else {
            GradientDrawable border = new GradientDrawable();
            border.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
            border.setStroke(5, ContextCompat.getColor(this, R.color.colorPrimary));
            border.setCornerRadius(50);
            startSync.setBackground(border);

            startSync.setTextColor(ContextCompat.getColor(this, R.color.white));
            startSync.setText(R.string.btn_sync_start);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_play_bold);
            startSync.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
    }
    private void trackWorkStatus() {
        WorkManager.getInstance(this).getWorkInfosByTagLiveData(Constants.SyncType.ALL_DATA.toString()).observe(this, workInfos -> {
            if (hasRunningWork(workInfos)) {
                syncViewModel.setSyncRunning(true);
                loadingSpinner.setVisibility(View.VISIBLE);
                updateSyncButton(true);
            } else {
                syncViewModel.setSyncRunning(false);
                loadingSpinner.setVisibility(View.INVISIBLE);
                updateSyncButton(false);
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
        } else if (Constants.SyncStatus.SUCCESSFUL.toString().equals(status)) {
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
            SyncLogWithErrors log = syncViewModel.getSyncLog().getValue();
            List<SyncLogCount> logsByStatus = syncViewModel.getLogsCountByStatus().getValue();
            boolean hasSuccess = false;
            if (logsByStatus != null) {
                   for (SyncLogCount syncLogCount : logsByStatus) {
                    if (Constants.SyncStatus.SUCCESSFUL.toString().equals(syncLogCount.status)
                            && syncLogCount.logCount > 0) {
                        hasSuccess = true;
                    }
                }
            }

            if (log !=null && log.syncLog != null && !hasSuccess
                    &&  !Constants.SyncStatus.SUCCESSFUL.toString().equals(log.syncLog.getStatus())) {
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