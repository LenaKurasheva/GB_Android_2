package ru.geekbrains.gb_android_2.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// Описание объекта, обрабатывающего данные
// @Dao - доступ к данным
// В этом классе описывается, как будет происходить обработка данных
    @Dao
    public interface CitiesListDao {

        // Метод для добавления города в базу данных
        // @Insert - признак добавления
        // onConflict - что делать, если такая запись уже есть
        // В данном случае просто заменим её
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertCity(CitiesList city);

        // Метод для замены данных города
        @Update
        void updateCity(CitiesList city);

        // Удаляем данные города
        @Delete
        void deleteCity(CitiesList city);

        // Удаляем данные города, зная ключ
        @Query("DELETE FROM citieslist WHERE id = :id")
        void deteleCityById(long id);

        // Забираем данные по всем городам
        @Query("SELECT * FROM citieslist ORDER BY created DESC")
        List<CitiesList> getAllCities();

        // Получаем данные одного города по id
        @Query("SELECT * FROM citieslist WHERE id = :id")
        CitiesList getCityById(long id);

        // Получаем данные одного города по name
        @Query("SELECT * FROM citieslist WHERE city = :name")
        CitiesList getCityByName(String name);

        //Получаем количество записей в таблице
        @Query("SELECT COUNT() FROM citieslist")
        long getCountCities();

        @Query("update citieslist set created = :currentTime where city = :cityName")
        void updateCreatedTime(String cityName, long currentTime);
    }


