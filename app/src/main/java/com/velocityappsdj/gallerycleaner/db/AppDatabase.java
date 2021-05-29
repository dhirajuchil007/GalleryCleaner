package com.velocityappsdj.gallerycleaner.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Data.class, exportSchema = false, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static final String db_name = "app_db";
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, db_name).fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract DataDao dataDao();
}
