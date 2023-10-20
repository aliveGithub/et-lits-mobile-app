package org.moa.etlits.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.moa.etlits.R;
import org.moa.etlits.data.repositories.EstablishmentRepository;
import org.moa.etlits.databinding.ActivityEstablishmentSummaryBinding;
import org.moa.etlits.utils.Constants;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class EstablishmentSummaryActivity extends AppCompatActivity {
    private ActivityEstablishmentSummaryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEstablishmentSummaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initActionBar();

        String code = getIntent().getStringExtra("code");
        boolean viewMode = getIntent().getBooleanExtra("isViewMode", false);
        if(viewMode) {
            binding.btnSetDefault.setVisibility(Button.GONE);
        } else {
            binding.btnSetDefault.setVisibility(Button.VISIBLE);
        }

        binding.btnSetDefault.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constants.DEFAULT_ESTABLISHMENT, code);
            editor.apply();
            Toast.makeText(this, getString(R.string.establishment_set_default_msg, code), Toast.LENGTH_SHORT).show();
            finish();
        });


        EstablishmentRepository establishmentRepository = new EstablishmentRepository(this.getApplication());
        establishmentRepository.loadByCode(code).observe(this, establishment -> {
            if (establishment != null) {
                binding.tvPic.setText(getDisplayText(establishment.getCode()));
                binding.tvName.setText(getDisplayText(establishment.getName()));
                binding.tvType.setText(getDisplayText(establishment.getType()));
                binding.tvLatLng.setText(getDisplayText(establishment.getLatLng()));
                binding.tvPhysicalAddress.setText(getDisplayText(establishment.getPhysicalAddress()));
                binding.tvPostalAddress.setText(getDisplayText(establishment.getAlternativePostalAddress()));
                binding.tvPhone.setText(getDisplayText(establishment.getTelephoneNumber()));
                binding.tvMobile.setText(getDisplayText(establishment.getMobileNumber()));
                binding.tvEmail.setText(getDisplayText(establishment.getEmail()));
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
          onBackPressed();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
