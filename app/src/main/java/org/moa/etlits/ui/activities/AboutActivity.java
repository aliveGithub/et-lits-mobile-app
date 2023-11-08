package org.moa.etlits.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.moa.etlits.BuildConfig;
import org.moa.etlits.R;

public class AboutActivity extends AppCompatActivity {

    private ActionBar actionBar;

    private TextView deviceModelTxt;
    private TextView androidVersionTxt;
    private TextView buildVersionTxt;
    private TextView buildDateTxt;

    private Button termsOfUseBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        deviceModelTxt = (TextView) findViewById(R.id.deviceModelTxt);
        androidVersionTxt = (TextView) findViewById(R.id.androidVersionTxt);
        buildVersionTxt = (TextView) findViewById(R.id.buildVirsionTxt);
        buildDateTxt = (TextView) findViewById(R.id.buildDateTxt);

        termsOfUseBtn = (Button) findViewById(R.id.touBtn);

        termsOfUseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTermsOfUse();
            }
        });

        displayPhoneInfo();
        setUpActionBar();
    }

    private void showTermsOfUse() {
        Intent intent = new Intent(AboutActivity.this, TermsOfUseActivity.class);
        intent.putExtra("screenModeAccept", false);
        startActivity(intent);
    }

    private void displayPhoneInfo() {
        String deviceModel = Build.MODEL;
         String androidVersion = Build.VERSION.RELEASE;
         String buildVersion = BuildConfig.VERSION_NAME;
         String buildDate = BuildConfig.BUILD_TIME;



         deviceModelTxt.setText(getString(R.string.device_model, deviceModel));
         androidVersionTxt.setText(getString(R.string.android_virsion, androidVersion));
         buildVersionTxt.setText(getString(R.string.build_virsion, buildVersion));

        buildDateTxt.setText(getString(R.string.build_date, buildDate));

    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();

        if(actionBar != null) {

            actionBar.setTitle(getString(R.string.about_et_lits));
            actionBar.setDisplayHomeAsUpEnabled(true);

            final Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);

            if(upArrow != null) {
                upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark, getTheme()), PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}