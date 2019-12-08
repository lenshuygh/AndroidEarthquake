package com.lens.profandroidbook.myearthquake;

import android.content.Context;

import androidx.room.Room;

public class EarthquakeDatabaseAccessor {
    public static EarthquakeDatabase earthquakeDatabaseInstance;
    public static final String EARTHQUAKE_DB_NAME = "earthquake_db";

    private EarthquakeDatabaseAccessor() {
    }

    public static EarthquakeDatabase getInstance(Context context) {
        if (earthquakeDatabaseInstance == null) {
            // create or open new SQLiteDB
            // return it as a Room DB instance
            earthquakeDatabaseInstance = Room.databaseBuilder(
                    context,
                    EarthquakeDatabase.class,
                    EARTHQUAKE_DB_NAME
            ).build();
        }

        return earthquakeDatabaseInstance;
    }
}
