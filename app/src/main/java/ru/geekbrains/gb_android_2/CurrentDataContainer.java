package ru.geekbrains.gb_android_2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

import ru.geekbrains.gb_android_2.model.HourlyWeatherData;
import ru.geekbrains.gb_android_2.model.WeatherData;

public class CurrentDataContainer implements Serializable {

    //Внутреннее поле, будет хранить единственный экземпляр
    private static CurrentDataContainer instance = null;

    // Поле для синхронизации
    private static final Object syncObj = new Object();
    // Конструктор (вызывать извне его нельзя, поэтому он приватный)
    private CurrentDataContainer(){}

    // Метод, который возвращает экземпляр объекта.
    // Если объекта нет, то создаем его.
    public static CurrentDataContainer getInstance(){
        // Здесь реализована «ленивая» инициализация объекта,
        // то есть, пока объект не нужен, не создаем его.
        synchronized (syncObj) {
            if (instance == null) {
                instance = new CurrentDataContainer();
            }
            return instance;
        }
    }

    String currCityName = "Saint Petersburg";
    boolean[] switchSettingsArray;
    ArrayList<WeatherData> weekWeatherData = new ArrayList<>();
    ArrayList<HourlyWeatherData> hourlyWeatherList;
    ArrayList<String> citiesList = new ArrayList<>();
    static boolean isFirstEnter = true;
    static boolean isNightModeOn;
    static boolean NightIsAlreadySettedInMain;
    static Stack<String> backStack = new Stack<>();
}
