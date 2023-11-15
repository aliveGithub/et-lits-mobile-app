package org.moa.etlits.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.ui.fragments.HomeTabsFragment;
import org.moa.etlits.ui.viewmodels.HomeViewModel;
import org.moa.etlits.utils.Constants;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {
    private HomeViewModel homeViewModel;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new HomeTabsFragment())
                .commit();

        homeViewModel = new ViewModelProvider(MainActivity.this, new HomeViewModel.HomeViewModelFactory(getApplication()))
                .get(HomeViewModel.class);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        boolean hasInitialized = sharedPreferences.getBoolean(Constants.HAS_INITIALIZED, false);
        if (!hasInitialized) {
            boolean initialSyncStarted = sharedPreferences.getBoolean(Constants.INITIAL_SYNC_COMPLETED, false);
            showDataInitDialog(initialSyncStarted);
            homeViewModel.setInitDialogShown(true);
        }
    }

    private void showDataInitDialog(boolean syncAttempted) {
        final Dialog customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.custom_dialog);
        customDialog.setCancelable(false);

        TextView title = customDialog.findViewById(R.id.dialog_title);
        TextView message = customDialog.findViewById(R.id.dialog_message);

        Button positiveButton = customDialog.findViewById(R.id.positive_button);
        Button negativeButton = customDialog.findViewById(R.id.negative_button);
        Button neutralButton = customDialog.findViewById(R.id.neutral_button);

        title.setText(R.string.sync_init_dialog_title);
        message.setText(syncAttempted ? R.string.sync_init_dialog_msg_resume : R.string.sync_init_dialog_msg);

        positiveButton.setText(R.string.sync_start_data_download);
        positiveButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SyncActivity.class);
            intent.putExtra("syncType", Constants.SyncType.ALL_DATA.toString());
            intent.putExtra("startSync", true);
            startActivity(intent);
            customDialog.dismiss();
        });

        negativeButton.setText(R.string.sync_exit_app);
        negativeButton.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });

        neutralButton.setVisibility(View.GONE);

        customDialog.show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
