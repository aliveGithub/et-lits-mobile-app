package org.moa.etlits.ui.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import org.moa.etlits.R;
import org.moa.etlits.data.repositories.EstablishmentRepository;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class EstablishmentSummaryActivity extends AppCompatActivity {
    private TextView pic;
    private TextView name;
    private TextView type;
    private TextView latLong;
    private TextView physicalAddress;
    private TextView postalAddress;
    private TextView phone;
    private TextView mobile;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_summary);
        initActionBar();

        pic = findViewById(R.id.tv_pic);
        name = findViewById(R.id.tv_name);
        type = findViewById(R.id.tv_type);
        latLong = findViewById(R.id.tv_lat_lng);
        physicalAddress = findViewById(R.id.tv_physical_address);
        postalAddress = findViewById(R.id.tv_postal_address);
        phone = findViewById(R.id.tv_phone);
        mobile = findViewById(R.id.tv_mobile);
        email = findViewById(R.id.tv_email);

        String code = getIntent().getStringExtra("code");
        EstablishmentRepository establishmentRepository = new EstablishmentRepository(this.getApplication());
        establishmentRepository.loadByCode(code).observe(this, establishment -> {
            if (establishment != null) {
                pic.setText(getDisplayText(establishment.getCode()));
                name.setText(getDisplayText(establishment.getName()));
                type.setText(getDisplayText(establishment.getType()));
                latLong.setText(getDisplayText(establishment.getLatLng()));
                physicalAddress.setText(getDisplayText(establishment.getPhysicalAddress()));
                postalAddress.setText(getDisplayText(establishment.getAlternativePostalAddress()));
                phone.setText(getDisplayText(establishment.getTelephoneNumber()));
                mobile.setText(getDisplayText(establishment.getMobileNumber()));
                email.setText(getDisplayText(establishment.getEmail()));
            }
        });
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.establishment_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            if (upArrow != null) {
                upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark, getTheme()), PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
          EstablishmentSummaryActivity.super.onBackPressed();
          return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private String getDisplayText(String text) {
        if (TextUtils.isEmpty(text)) {
            return getString(R.string.establishment_not_specified);
        }

        return text;
    }
}
