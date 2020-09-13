package ru.geekbrains.gb_android_2;

import android.app.Application;

import androidx.room.Room;

import ru.geekbrains.gb_android_2.database.CitiesListDao;
import ru.geekbrains.gb_android_2.database.CitiesListDatabase;

// паттерн синглтон, наследуем класс Application
// создаем базу данных в методе onCreate
public class App extends Application {

    private static App instance;

    // база данных
    private CitiesListDatabase db;

    // Так получаем объект приложения
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Это для синглтона, сохраняем объект приложения
        instance = this;

        // строим базу
        db = Room.databaseBuilder(
                getApplicationContext(),
                CitiesListDatabase.class,
                "cities_list_database")
//                .allowMainThreadQueries() //Только для примеров и тестирования, в реальной разработке нужен новый поток
//                .addMigrations(new Migration_1_2()) // Пока не создана
                .build();
    }

    // Получаем EducationDao для составления запросов
    public CitiesListDao getCitiesListDao() {
        return db.getCitiesListDao();
    }
}
