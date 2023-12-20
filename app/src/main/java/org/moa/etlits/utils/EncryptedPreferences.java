package org.moa.etlits.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

public class EncryptedPreferences {
    private final String ENCRYPTED_FILE_NAME = "et-lits-encrypted-prefs";
    private SharedPreferences mEncryptedSharedPref;
    public EncryptedPreferences(Context context) {
        try {
            String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            mEncryptedSharedPref = EncryptedSharedPreferences.create(
                        ENCRYPTED_FILE_NAME,
                        masterKey,
                        context,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
        } catch (NoClassDefFoundError | Exception e) {
           Log.e("EncryptedPreferences", e.getMessage());
        }
    }

    public void write(String key, String value) {
        if (mEncryptedSharedPref != null) {
            SharedPreferences.Editor editor = mEncryptedSharedPref.edit();
            editor.putString(key, value).apply();
        }
    }

    public void write(String key, Set<String> value) {
        if (mEncryptedSharedPref != null) {
            SharedPreferences.Editor editor = mEncryptedSharedPref.edit();
            editor.putStringSet(key, value).apply();
        }
    }


    public String read(String key) {
        if (mEncryptedSharedPref != null) {
            return mEncryptedSharedPref.getString(key, "");
        }
        return "";
    }

    public Set<String> readSet(String key) {
        if (mEncryptedSharedPref != null) {
            return mEncryptedSharedPref.getStringSet(key, new HashSet<>());
        }
        return new HashSet<>();
    }
    public void remove(String key) {
        if (mEncryptedSharedPref != null) {
            SharedPreferences.Editor editor = mEncryptedSharedPref.edit();
            editor.remove(key);
            editor.apply();
        }
    }
}
