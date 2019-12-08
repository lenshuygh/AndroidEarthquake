package com.lens.profandroidbook.myearthquake;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Earthquake.class},version = 1)
@TypeConverters({EarthquakeTypeConverters.class})
public abstract class EarthquakeDatabase extends RoomDatabase {
    public abstract EarthquakeDao earthquakeDao();
}
