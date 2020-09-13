package ru.geekbrains.gb_android_2.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CitiesList.class}, version = 1)
    public abstract class CitiesListDatabase extends RoomDatabase {
        public abstract CitiesListDao getCitiesListDao();
    }


