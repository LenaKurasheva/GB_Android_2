package ru.geekbrains.gb_android_2.forecastRequest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenWeatherRepo {
    private static OpenWeatherRepo instance = null;
    private OpenWeatherInterface API;

    private OpenWeatherRepo() {
        API = createAdapter();
    }

    public static OpenWeatherRepo getInstance() {
        if(instance == null) {
            instance = new OpenWeatherRepo();
        }
        return instance;
    }

    public OpenWeatherInterface getAPI() {
        return API;
    }

    private OpenWeatherInterface createAdapter() {
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return adapter.create(OpenWeatherInterface.class);
    }
}

