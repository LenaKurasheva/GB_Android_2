package ru.geekbrains.gb_android_2.forecastRequest;

import android.util.Log;
import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.geekbrains.gb_android_2.model.weather.WeatherRequest;

public class ForecastRequest {

    public static int responseCode;
    private static WeatherRequest weatherRequest;
    private static CountDownLatch forecastResponseReceived;

    public static WeatherRequest getWeatherRequest(){return weatherRequest;}

    public static CountDownLatch getForecastResponseReceived(){return forecastResponseReceived;}

    public static void getForecastFromServer(Double lat, Double lon) {
        forecastResponseReceived = new CountDownLatch(1);
        Log.d("retrofit", "countDounLatch = " + forecastResponseReceived.getCount());
       OpenWeatherRepo.getInstance().getAPI().loadWeather(lat, lon,"metric",
                "2a72f5f940375d439b4598c5184c5e82").enqueue(new Callback<WeatherRequest>() {
            @Override
            public void onResponse(@NonNull Call<WeatherRequest> call,
                                   @NonNull Response<WeatherRequest> response) {
                if (response.body() != null && response.isSuccessful()) {
                    responseCode = 200;
                    weatherRequest = response.body();
                    forecastResponseReceived.countDown();
                    Log.d("retrofit", "countDown");

                } else {
                    //Похоже, код у нас не в диапазоне [200..300) и случилась ошибка
                    //обрабатываем ее
                    if (response.code() == 404) {
                        responseCode = 404;
                        forecastResponseReceived.countDown();
                        return;
                    }
                    if(response.code() == 400) {
                        responseCode = 400;
                        forecastResponseReceived.countDown();
                        return;
                    }
                    responseCode = 0;
                    forecastResponseReceived.countDown();
                    Log.d("retrofit", "response.code =" + " " + response.code());
                }
                Log.d("retrofit", "response.code = " + responseCode);
                Log.d("retrofit", "weatherRequest is null: " + (weatherRequest == null));
            }

            //сбой при интернет подключении
            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                Log.d("retrofit", "THROWABLE: " + t.toString());
                forecastResponseReceived.countDown();
                responseCode = 0;
            }
       });
    }
}
