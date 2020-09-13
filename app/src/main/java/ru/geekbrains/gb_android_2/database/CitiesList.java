package ru.geekbrains.gb_android_2.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// @Entity - это признак табличного объекта, то есть объект будет сохраняться
// в базе данных в виде строки
// indices указывает на индексы в таблице
    @Entity(indices = {@Index(value = {"city"})})
    public class CitiesList {

        // @PrimaryKey - указывает на ключевую запись,
        // autoGenerate = true - автоматическая генерация ключа
        @PrimaryKey(autoGenerate = true)
        public long id;

        // Имя города
        // @ColumnInfo позволяет задавать параметры колонки в БД
        // name = "city" - имя колонки
        @ColumnInfo(name = "city")
        public String name;

        // Тут будет храниться время добавления города в секундах
        // Это поле нужно для сортировки по времени добавления
        @ColumnInfo(name = "created")
        public long created;

        public CitiesList(){}

        public CitiesList(String cityName){this.name = cityName; this.created = System.currentTimeMillis()/1000L;}
    }


