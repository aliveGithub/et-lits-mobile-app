package org.moa.etlits.ui.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.moa.etlits.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SyncInfoActivity extends AppCompatActivity {
     private Button closeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_info);
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

        closeButton = findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
