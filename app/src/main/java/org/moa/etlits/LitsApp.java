package org.moa.etlits;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.text.TextUtils;

import java.util.Locale;

public class LitsApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        /*SharedPreferences preferences = getSharedPreferences(LanguageActivity.PREFS_FILENAME, MODE_PRIVATE);
        String lang = preferences.getString("lang", "");
        if (!TextUtils.isEmpty(lang)) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }*/

    }
}
