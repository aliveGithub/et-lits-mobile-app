package org.moa.etlits.ui.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import org.moa.etlits.utils.DateUtils;
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
import androidx.work.WorkInfo;
import androidx.work.WorkManager;


public class SyncActivity extends AppCompatActivity {
    private TextView statusTextView;

    private TextView lastSync;

    private TextView internetStatus;

    private TextView recordsToSend;

    private TextView stoppedByUser;

    private Button startSync;

    private ProgressBar loadingSpinner;

    private TextView recordsSent;

    private TextView recordsNotSent;

    private TextView recordsReceived;

    private TextView staleData;

    private ListView errorsList;

    private SyncViewModel syncViewModel;
    private SyncErrorAdapter syncErrorAdapter;
    private SharedPreferences sharedPreferences = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        initViews();
        initViewModels();
        addEventListeners();
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);

        boolean startSync = getIntent().getBooleanExtra("startSync", false);
        if (startSync && savedInstanceState == null) {
            startSync();
            trackWorkStatus();
        }
    }

    private void initViews() {
        setUpActionBar();
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

        InternetConnectionChecker internetConnectionCheck = new InternetConnectionChecker((ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE));
        internetConnectionCheck.observe(this, isConnected -> {
            if (isConnected) {
                internetStatus.setVisibility(View.INVISIBLE);
            } else {
                internetStatus.setVisibility(View.VISIBLE);
            }
        });

        updateUI(null);
    }

    private void setUpActionBar() {
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
    }

    private void initViewModels() {
        syncViewModel = new ViewModelProvider(this, new SyncViewModel.SyncViewModelFactory(getApplication())).get(SyncViewModel.class);
        syncViewModel.getSyncLog().observe(this, this::updateUI);
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
        syncViewModel.getSyncLog().observe(this, syncLog -> updateUI(syncLog));

        SyncWorkManager.startSync(this, syncViewModel.getCurrentSyncId().getValue(), Constants.SyncType.ALL_DATA.toString());
    }

    private void updateSyncButton(boolean isRunning) {
        GradientDrawable border = new GradientDrawable();
        border.setColor(ContextCompat.getColor(this, isRunning ? R.color.white : R.color.colorPrimary));
        border.setStroke(5, ContextCompat.getColor(this, R.color.colorPrimary));
        border.setCornerRadius(50);
        startSync.setBackground(border);
        startSync.setTextColor(ContextCompat.getColor(this, isRunning ? R.color.colorPrimary : R.color.white));
        startSync.setText(isRunning ? R.string.btn_sync_stop : R.string.btn_sync_start);

        Drawable drawable = ContextCompat.getDrawable(this, isRunning ? R.drawable.ic_stop_bold : R.drawable.ic_play_bold);
        startSync.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
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

    @Override
    protected void onResume() {
        super.onResume();
        trackWorkStatus();
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
            statusTextView.setText(getStatusMessage(log.syncLog.getStatus()));

            lastSync.setVisibility(View.VISIBLE);
            String formattedDate = log.syncLog.getLastSync() == null ? "" : dateFormat.format(log.syncLog.getLastSync());
            lastSync.setText(getString(R.string.sync_date,formattedDate));

            boolean wasStopped =  Constants.SyncStatus.STOPPED.toString().equals(log.syncLog.getStatus());
            stoppedByUser.setVisibility( wasStopped ? View.VISIBLE : View.GONE);

            recordsToSend.setText(String.valueOf(log.syncLog.getRecordsToSend()));
            recordsSent.setText(getString(R.string.sync_records_sent, String.valueOf(log.syncLog.getRecordsSent())));
            recordsNotSent.setText(getString(R.string.sync_records_not_sent, String.valueOf(log.syncLog.getRecordsNotSent())));
            recordsReceived.setText(getString(R.string.sync_records_received, String.valueOf(log.syncLog.getRecordsReceived())));

            long daysSinceLastSync = DateUtils.daysBetweenDates(log.syncLog.getLastSync(), new Date());
            staleData.setVisibility(daysSinceLastSync > 5 ? View.VISIBLE : View.GONE);

            syncErrorAdapter.clear();
            if (log.errors != null) {
                syncErrorAdapter.addAll(log.errors);
                syncErrorAdapter.notifyDataSetChanged();
            }

            updateSyncButton(syncViewModel.getSyncRunning());
        } else {
            staleData.setVisibility(View.GONE);

            lastSync.setVisibility(View.INVISIBLE);
            stoppedByUser.setVisibility(View.GONE);

            recordsSent.setText(getString(R.string.sync_records_sent, String.valueOf(0)));
            recordsNotSent.setText(getString(R.string.sync_records_not_sent, String.valueOf(0)));
            recordsReceived.setText(getString(R.string.sync_records_received, String.valueOf(0)));
            updateSyncButton(false);
        }
    }

    private String getStatusMessage(String status) {
        if (Constants.SyncStatus.INITIALIZING.toString().equals(status)) {
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
        inflater.inflate(R.menu.default_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            boolean hasInitialized = sharedPreferences.getBoolean(Constants.HAS_INITIALIZED, false);
            if (syncViewModel.getSyncRunning() && !hasInitialized) {
                showDataInitDialog();
            } else {
                this.onBackPressed();
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
        positiveButton.setOnClickListener(v -> customDialog.dismiss());

        customDialog.show();
    }
}