package org.moa.etlits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {
    private Button btnAmharic;

    private Button btnEnglish;
    private TextView tvAppName;

    public static final String PREFS_FILENAME = "org.moa.etlits.activities.SelectedLanguage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        btnAmharic = findViewById(R.id.btnAmharic);
        btnEnglish = findViewById(R.id.btnEnglish);
        tvAppName = findViewById(R.id.tvAppName);
        btnAmharic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //LocaleHelper.setLocale(MainActivity.this, "am");

                /*Locale locale = new Locale("am");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());*/

                //restartActivity();
                //.setText(resources.getString(R.string.app_name));
                saveSelectedLanguage("am");
                setLocale("am");
            }
        });

        btnEnglish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //LocaleHelper.setLocale(MainActivity.this, "en-US");
                //resources = context.getResources();
                /*Locale locale = new Locale("en");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());*/

                //restartActivity();
                //.setText(resources.getString(R.string.app_name));
                saveSelectedLanguage("en");
                setLocale("en");
            }
        });
    }

        private void saveSelectedLanguage(String lang) {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_FILENAME, MODE_PRIVATE)
                    .edit();
            editor.putString("lang", lang);
            editor.commit();
        }
        private void setLocale(String lang) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

            // restart activity to apply changes
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

}