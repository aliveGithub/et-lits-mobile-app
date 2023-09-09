package org.moa.etlits.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.ui.fragments.HomeTabsFragment;
import org.moa.etlits.ui.viewmodels.HomeViewModel;
import org.moa.etlits.utils.Constants;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {
    private HomeViewModel homeViewModel;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new HomeTabsFragment())
                .commit();

           homeViewModel = new ViewModelProvider(MainActivity.this, new HomeViewModel.HomeViewModelFactory(getApplication()))
                   .get(HomeViewModel.class);
           homeViewModel.getConfigDataSyncLog().observe(this, syncLog -> {
               if (syncLog == null) {
                   showDataInitDialog(null);
                   homeViewModel.setInitDialogShown(true);
               } else {
                   if (Constants.SyncStatus.IN_PROGRESS.toString().equals(syncLog.getStatus())
                   && !homeViewModel.getInitDialogShown()) {
                       showDataInitDialog(syncLog.getId());
                   }
               }
           });
    }

    private void showDataInitDialog(String syncLogId) {
        final Dialog customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.custom_dialog);
        customDialog.setCancelable(false);

        TextView title = customDialog.findViewById(R.id.dialog_title);
        Button positiveButton = customDialog.findViewById(R.id.positive_button);
        TextView message = customDialog.findViewById(R.id.dialog_message);
        Button negativeButton = customDialog.findViewById(R.id.negative_button);
        Button neutralButton = customDialog.findViewById(R.id.neutral_button);

        negativeButton.setText(R.string.sync_exit_app);
        negativeButton.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });

        title.setText(R.string.sync_init_dialog_title);
        if (syncLogId != null) {
            message.setText(R.string.sync_init_dialog_msg_resume);
            positiveButton.setText(R.string.sync_resume_data_download);
            positiveButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SyncActivity.class);
                intent.putExtra("syncLogId", syncLogId);
                intent.putExtra("syncType", Constants.SyncType.CONFIG_DATA.toString());
                startActivity(intent);
                customDialog.dismiss();
            });

            neutralButton.setVisibility(View.VISIBLE);
            neutralButton.setText(R.string.sync_view_last_attempt);
            neutralButton.setOnClickListener(v -> {
                customDialog.dismiss();
            });
        } else {
            message.setText(R.string.sync_init_dialog_msg);
            positiveButton.setText(R.string.sync_start_data_download);
            positiveButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SyncActivity.class);
                intent.putExtra("syncType", Constants.SyncType.CONFIG_DATA.toString());
                startActivity(intent);
                customDialog.dismiss();
            });
            neutralButton.setVisibility(View.GONE);
        }

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
