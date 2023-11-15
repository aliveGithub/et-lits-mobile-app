package org.moa.etlits.data.dao;

import static android.content.Context.MODE_PRIVATE;

import org.moa.etlits.BuildConfig;
import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.models.AnimalRegistration;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.data.models.Establishment;
import org.moa.etlits.data.models.SyncError;
import org.moa.etlits.data.models.SyncLog;
import org.moa.etlits.data.models.Treatment;
import org.moa.etlits.utils.Constants;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                Animal.class, SyncLog.class, SyncError.class, CategoryValue.class,
                Establishment.class, AnimalRegistration.class, Treatment.class
        },
        version = 2
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "et_lits_database";
    private static AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public synchronized static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    private static AppDatabase buildDatabase(Context context) {
        Builder<AppDatabase> builder = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, DATABASE_NAME);

        if (BuildConfig.DEBUG) {
            builder.fallbackToDestructiveMigration();
        }

        builder.addCallback(new Callback() {
            @Override
            public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
                super.onDestructiveMigration(db);
                scheduleReinitialization(context);
            }
        });

        return builder.build();
    }

    private static void scheduleReinitialization(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constants.SHARED_PREFERENCES, MODE_PRIVATE);

        preferences.edit()
                .putBoolean(Constants.HAS_INITIALIZED, false)
                .putBoolean(Constants.INITIAL_SYNC_COMPLETED, false)
                .apply();
    }

    public abstract AnimalDao animalDao();

    public abstract SyncLogDao syncLogDao();

    public abstract CategoryValueDao categoryValueDao();

    public abstract EstablishmentDao establishmentDao();

    public abstract AnimalRegistrationDao animalRegistrationDao();

    public abstract  TreatmentDao treatmentDao();
}
