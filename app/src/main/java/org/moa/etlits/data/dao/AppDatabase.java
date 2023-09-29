package org.moa.etlits.data.dao;

import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.models.AnimalRegistration;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.data.models.Establishment;
import org.moa.etlits.data.models.SyncError;
import org.moa.etlits.data.models.SyncLog;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Animal.class, SyncLog.class, SyncError.class, CategoryValue.class, Establishment.class, AnimalRegistration.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "et_lits_database";
    private static AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                                    DATABASE_NAME)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                //WordDao dao = INSTANCE.wordDao();
                //dao.deleteAll();

                //Word word = new Word("Hello");
                //dao.insert(word);
               // word = new Word("World");
                //dao.insert(word);
            });
        }
    };
    public abstract AnimalDao animalDao();
    public abstract SyncLogDao syncLogDao();

    public abstract CategoryValueDao categoryValueDao();

    public abstract EstablishmentDao establishmentDao();

    public abstract AnimalRegistrationDao animalRegistrationDao();
}
