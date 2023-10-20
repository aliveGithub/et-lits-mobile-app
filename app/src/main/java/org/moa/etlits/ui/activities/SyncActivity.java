package org.moa.etlits.ui.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.SyncLog;
import org.moa.etlits.data.models.SyncLogWithErrors;
import org.moa.etlits.databinding.ActivitySyncBinding;
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

    private ActivitySyncBinding binding;

    private SyncViewModel syncViewModel;
    private SyncErrorAdapter syncErrorAdapter;
    private SharedPreferences sharedPreferences = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        syncErrorAdapter = new SyncErrorAdapter(this, new ArrayList<>());
        binding.lstErrors.setAdapter(syncErrorAdapter);

        InternetConnectionChecker internetConnectionCheck = new InternetConnectionChecker((ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE));
        internetConnectionCheck.observe(this, isConnected -> {
            if (isConnected) {
                binding.tvNetworkStatus.setVisibility(View.INVISIBLE);
            } else {
                binding.tvNetworkStatus.setVisibility(View.VISIBLE);
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
        binding.btnSync.setOnClickListener(v -> {
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
        binding.btnSync.setText(isRunning ? R.string.btn_sync_stop : R.string.btn_sync_start);
        binding.btnSync.setTextColor(ContextCompat.getColor(this, isRunning ? R.color.colorPrimary : R.color.white));
        binding.btnSync.setBackground(ContextCompat.getDrawable(this, isRunning ? R.drawable.btn_green_white : R.drawable.btn_primary_green));
        Drawable drawable = ContextCompat.getDrawable(this, isRunning ? R.drawable.ic_stop_bold : R.drawable.ic_play_bold);
        binding.btnSync.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    private void trackWorkStatus() {
        WorkManager.getInstance(this).getWorkInfosByTagLiveData(Constants.SyncType.ALL_DATA.toString()).observe(this, workInfos -> {
            if (hasRunningWork(workInfos)) {
                syncViewModel.setSyncRunning(true);
                binding.pbLoading.setVisibility(View.VISIBLE);
                updateSyncButton(true);
            } else {
                syncViewModel.setSyncRunning(false);
                binding.pbLoading.setVisibility(View.INVISIBLE);
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
            binding.tvStatus.setText(getStatusMessage(log.syncLog.getStatus()));

            String formattedDate = log.syncLog.getLastSync() == null ? "" : dateFormat.format(log.syncLog.getLastSync());
            binding.tvLastSync.setVisibility(View.VISIBLE);
            binding.tvLastSync.setText(getString(R.string.sync_date,formattedDate));

            boolean wasStopped =  Constants.SyncStatus.STOPPED.toString().equals(log.syncLog.getStatus());
            binding.tvStoppedByUser.setVisibility( wasStopped ? View.VISIBLE : View.GONE);
            binding.tvRecordsCount.setText(String.valueOf(log.syncLog.getRecordsToSend()));
            binding.tvRecordsSent.setText(getString(R.string.sync_records_sent, String.valueOf(log.syncLog.getRecordsSent())));
            binding.tvRecordsNotSent.setText(getString(R.string.sync_records_not_sent, String.valueOf(log.syncLog.getRecordsNotSent())));
            binding.tvRecordsReceived.setText(getString(R.string.sync_records_received, String.valueOf(log.syncLog.getRecordsReceived())));

            long daysSinceLastSync = DateUtils.daysBetweenDates(log.syncLog.getLastSync(), new Date());
            binding.tvStaleData.setVisibility(daysSinceLastSync > 5 ? View.VISIBLE : View.GONE);

            syncErrorAdapter.clear();
            if (log.errors != null) {
                syncErrorAdapter.addAll(log.errors);
                syncErrorAdapter.notifyDataSetChanged();
            }

            updateSyncButton(syncViewModel.getSyncRunning());
        } else {
            binding.tvStaleData.setVisibility(View.GONE);
            binding.tvLastSync.setVisibility(View.INVISIBLE);
            binding.tvRecordsSent.setText(getString(R.string.sync_records_sent, String.valueOf(0)));
            binding.tvRecordsNotSent.setText(getString(R.string.sync_records_not_sent, String.valueOf(0)));
            binding.tvRecordsReceived.setText(getString(R.string.sync_records_received, String.valueOf(0)));
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
        } else if (Constants.SyncStatus.PARTIAL.toString().equals(status)) {
            return getString(R.string.sync_status_partial);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}