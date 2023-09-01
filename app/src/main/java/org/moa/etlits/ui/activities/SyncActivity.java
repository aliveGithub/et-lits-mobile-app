package org.moa.etlits.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.data.models.SyncLog;
import org.moa.etlits.data.repositories.CategoryValueRepository;
import org.moa.etlits.data.repositories.SyncLogRepository;
import org.moa.etlits.jobs.SyncWorkManager;
import org.moa.etlits.utils.Constants;

public class SyncActivity extends AppCompatActivity {
    private CategoryValueRepository categoryValueRepository;

    private SyncLogRepository syncLogRepository;

    private TextView statusTextView;

    private TextView lastSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_data_sync);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button startSync = findViewById(R.id.btn_sync);
        startSync.setOnClickListener(v ->  {
            SyncWorkManager.startSync(this);
        });

        /* categoryValueRepository = new CategoryValueRepository((Application) getApplicationContext());
        categoryValueRepository.getAllCategoryValues().observe(this, categoryValues -> {

            Log.i("SyncActivity:", categoryValues != null ? ""+ categoryValues.size() : "");

            if (categoryValues != null) {
                for (CategoryValue catV : categoryValues) {
                    Log.i("SyncActivity:", catV.getCategoryKey());
                    Log.i("SyncActivity:", catV.getValue());
                }
            }


        });*/

        TextView statusTextView = findViewById(R.id.tvStatus);
        TextView lastSync = findViewById(R.id.tvLastSync);
        syncLogRepository = new SyncLogRepository((Application) getApplicationContext());
        LiveData<SyncLog> syncLogData = syncLogRepository.loadByType(Constants.SyncType.CONFIG_DATA.toString()) ;
        syncLogData.observe(this, syncLog -> {
            if (syncLog != null) {
                lastSync.setText(syncLog.getStatus());
                statusTextView.setText(String.valueOf(syncLog.getLastSync()));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}