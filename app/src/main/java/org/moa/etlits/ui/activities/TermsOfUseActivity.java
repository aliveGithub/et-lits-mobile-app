package org.moa.etlits.ui.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.moa.etlits.R;
import org.moa.etlits.utils.Constants;
import org.moa.etlits.utils.EncryptedPreferences;

public class TermsOfUseActivity extends AppCompatActivity {

    private ActionBar actionBar;

    private Boolean screenModeAccept;
    private Button agreeBtn;
    private Button disagreeBtn;

    private LinearLayout agreeDisagreeBtnGroup;

    private SharedPreferences sharedPreferences;

    private EncryptedPreferences encryptedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);
        screenModeAccept = getIntent().getBooleanExtra("screenModeAccept", false);

        agreeBtn = (Button) findViewById(R.id.agree_btn);
        disagreeBtn = (Button) findViewById(R.id.disagree_btn);
        agreeDisagreeBtnGroup = (LinearLayout) findViewById(R.id.agree_disagree_btn_group);

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        encryptedPreferences = new EncryptedPreferences(TermsOfUseActivity.this);


        if(!screenModeAccept) {
            agreeDisagreeBtnGroup.setVisibility(View.GONE);
            agreeBtn.setVisibility(View.GONE);
            disagreeBtn.setVisibility(View.GONE);
        } else {


            agreeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agreeTermsOfUse();
                }
            });

            disagreeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    disagreeTermsOfUse();
                }
            });


        }

//        custom back navigation implementation
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                disagreeTermsOfUse();

            }
        };


        setUpActionBar();
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();

        if(actionBar != null) {

            actionBar.setTitle("Terms of Use");
            actionBar.setDisplayHomeAsUpEnabled(true);

            final Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);

            if(upArrow != null) {
                upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark, getTheme()), PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
            }

        }
    }
    private void agreeTermsOfUse() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.HAS_TERMS_OR_USE_ACCEPTED, true);
        editor.apply();

        Intent intent = new Intent(TermsOfUseActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void disagreeTermsOfUse() {
        encryptedPreferences.remove(Constants.USERNAME);
        encryptedPreferences.remove(Constants.PASSWORD);
        Intent intent = new Intent(TermsOfUseActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}