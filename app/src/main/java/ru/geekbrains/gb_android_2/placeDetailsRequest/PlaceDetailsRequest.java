package ru.geekbrains.gb_android_2.placeDetailsRequest;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.geekbrains.gb_android_2.model.placeDetails.FetchPlaceRequest;
import ru.geekbrains.gb_android_2.model.weather.WeatherRequest;

public class PlaceDetailsRequest {

    public static int responseCode;
    private static FetchPlaceRequest fetchPlaceRequest;
    public static CountDownLatch detailsResponseReceived;

    public static FetchPlaceRequest getFetchPlaceRequest(){return fetchPlaceRequest;}

    public static CountDownLatch getDetailsResponseReceived(){return detailsResponseReceived;}

    public static void getPlaceDetails(String id) {
        detailsResponseReceived = new CountDownLatch(1);
        Log.d("retrofit", "countDounLatch = " + detailsResponseReceived.getCount());
       PlaceDetailsRepo.getInstance().getAPI().loadPlaceDetails(id,"AIzaSyD3YSJ6LJLFjr94lDYfclthZ-ROzfAhVOI").enqueue(new Callback<FetchPlaceRequest>() {
            @Override
            public void onResponse(@NonNull Call<FetchPlaceRequest> call,
                                   @NonNull Response<FetchPlaceRequest> response) {
                if (response.body() != null && response.isSuccessful()) {
                    responseCode = 200;
                    fetchPlaceRequest = response.body();
                    detailsResponseReceived.countDown();
                    Log.d("retrofit", "countDown");

                } else {
                    //Похоже, код у нас не в диапазоне [200..300) и случилась ошибка
                    //обрабатываем ее
                    if (response.code() == 404) {
                        responseCode = 404;
                        detailsResponseReceived.countDown();
                        return;
                    }
                    responseCode = 0;
                    detailsResponseReceived.countDown();
                    Log.d("retrofit", "response.code =" + " " + response.code());
                }
                Log.d("retrofit", "response.code = " + responseCode);
                Log.d("retrofit", "weatherRequest is null: " + (fetchPlaceRequest == null));
            }

            //сбой при интернет подключении
            @Override
            public void onFailure(Call<FetchPlaceRequest> call, Throwable t) {
                Log.d("retrofit", "THROWABLE: " + t.toString());
                detailsResponseReceived.countDown();
                responseCode = 0;
            }
       });
    }
}
