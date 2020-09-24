package ru.geekbrains.gb_android_2.placeDetailsRequest;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import ru.geekbrains.gb_android_2.model.HourlyWeatherData;
import ru.geekbrains.gb_android_2.model.WeatherData;
import ru.geekbrains.gb_android_2.model.placeDetails.FetchPlaceRequest;
import ru.geekbrains.gb_android_2.model.weather.WeatherRequest;

public final class GooglePlaceDetails {
    private FetchPlaceRequest fetchPlaceRequest = new FetchPlaceRequest();
    private Double cityLatitude;
    private Double cityLongitude;

    public Double getCityLongitude(){return cityLongitude;}
    public Double getCityLatitude(){return cityLatitude;}

    //Внутреннее поле, будет хранить единственный экземпляр
    private static GooglePlaceDetails instance = null;

    // Поле для синхронизации
    private static final Object syncObj = new Object();

    // Конструктор (вызывать извне его нельзя, поэтому он приватный)
    private GooglePlaceDetails() {
    }

    // Метод, который возвращает экземпляр объекта.
    // Если объекта нет, то создаем его.
    public static GooglePlaceDetails getInstance() {
        // Здесь реализована «ленивая» инициализация объекта,
        // то есть, пока объект не нужен, не создаем его.
        synchronized (syncObj) {
            if (instance == null) {
                instance = new GooglePlaceDetails();
            }
            return instance;
        }
    }

    public  void getCityCoordinates() {
        fetchPlaceRequest = PlaceDetailsRequest.getFetchPlaceRequest();
        if (fetchPlaceRequest != null) {
            Log.d("Threads", "getCityCoordinates -> fetchPlaceRequest != null");
            Thread coorditatesGetter = new Thread(() -> {
                cityLatitude = fetchPlaceRequest.getResult().getGeometry().getLocation().getLat();
                cityLongitude = fetchPlaceRequest.getResult().getGeometry().getLocation().getLng();
            });
            coorditatesGetter.start();
            Log.d("Threads", "coorditatesGetter start");
            try {
                coorditatesGetter.join();
                Log.d("Threads", "coorditatesGetter joined");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}