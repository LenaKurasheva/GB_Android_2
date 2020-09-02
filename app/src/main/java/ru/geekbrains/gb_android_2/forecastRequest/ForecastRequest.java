package ru.geekbrains.gb_android_2.forecastRequest;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ru.geekbrains.gb_android_2.model.weather.WeatherRequest;

public class ForecastRequest {

    final static String myLog = "myLog";
    private static final String TAG = "WEATHER";
    public static int responseCode;
    private static WeatherRequest weatherRequest;

    public static WeatherRequest getWeatherRequest(){return weatherRequest;}

    public static void getForecastFromServer(String currentCity, URL forecastSourceUrl){
        try {
            final URL uri = forecastSourceUrl;
            Thread t1 = new Thread(() -> {
                HttpsURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpsURLConnection) uri.openConnection();
                    urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
                    urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
                    // Получаем ответ от сервера, если соединение невозможно, то обнуляем значение responseCode,
                    // чтобы не использовать устаревшее значение и отображалось уведомление об ошибке соединения:
                    try{
                        responseCode = urlConnection.getResponseCode();
                    } catch (Exception e){
                        responseCode = 0;
                    }
                    Log.d(myLog, "###getFiveDaysWeatherFromServer responseCod = " + responseCode);
                    Log.d(myLog, "###getFiveDaysWeatherFromServer currentCity = " + currentCity);
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
                    String result = getLines(in);
                    // преобразование данных запроса в модель
                    Gson gson = new Gson();
                    weatherRequest = gson.fromJson(result, WeatherRequest.class);
                    Log.d(myLog, "ChooseCityPresenter - getFiveDaysWeatherFromServer - getWeatherData ");
                } catch (Exception e) {
                    Log.e(TAG, "Fail connection", e);
                    e.printStackTrace();
                } finally {
                    if (null != urlConnection) {
                        urlConnection.disconnect();
                    }
                }
            });
            t1.start();
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String getLines(BufferedReader reader) {
        StringBuilder rawData = new StringBuilder(1024);
        String tempVariable;

        while (true) {
            try {
                tempVariable = reader.readLine();
                if (tempVariable == null) break;
                rawData.append(tempVariable).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawData.toString();
    }
}
