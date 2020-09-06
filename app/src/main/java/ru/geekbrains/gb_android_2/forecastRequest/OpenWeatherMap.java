package ru.geekbrains.gb_android_2.forecastRequest;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import ru.geekbrains.gb_android_2.model.HourlyWeatherData;
import ru.geekbrains.gb_android_2.model.WeatherData;
import ru.geekbrains.gb_android_2.model.weather.WeatherRequest;

public final class OpenWeatherMap {
    public static final int FORECAST_DAYS = 5;
    private WeatherRequest weatherRequest = new WeatherRequest();

    ArrayList<String> fiveDaysTempMax;
    ArrayList<String> fourDayTempMin;
    ArrayList<String> weatherStateInfoArrayList;
    ArrayList<Integer> weatherIconsArrayList;
    ArrayList<String> degreesArrayList;
    ArrayList<String> windInfoArrayList;
    ArrayList<String> pressureArrayList;
    ArrayList<String> feelLikeArrayList;

    //Внутреннее поле, будет хранить единственный экземпляр
    private static OpenWeatherMap instance = null;

    // Поле для синхронизации
    private static final Object syncObj = new Object();

    // Конструктор (вызывать извне его нельзя, поэтому он приватный)
    private OpenWeatherMap(){}

    // Метод, который возвращает экземпляр объекта.
    // Если объекта нет, то создаем его.
    public static OpenWeatherMap getInstance(){
        // Здесь реализована «ленивая» инициализация объекта,
        // то есть, пока объект не нужен, не создаем его.
        synchronized (syncObj) {
            if (instance == null) {
                instance = new OpenWeatherMap();
            }
            return instance;
        }
    }

    public ArrayList<WeatherData> getWeekWeatherData( Resources resources) {
        fiveDaysTempMax = new ArrayList<>();
        fourDayTempMin = new ArrayList<>();
        weatherStateInfoArrayList = new ArrayList<>();
        weatherIconsArrayList = new ArrayList<>();
        degreesArrayList = new ArrayList<>();
        windInfoArrayList = new ArrayList<>();
        pressureArrayList = new ArrayList<>();
        feelLikeArrayList = new ArrayList<>();
        ArrayList<WeatherData> weekWeatherData = new ArrayList<>();
        weatherRequest = ForecastRequest.getWeatherRequest();
        if(weatherRequest != null) {
            Log.d("Threads","getWeekWeatherData -> weatherRequest != null" );
            Thread weekData = new Thread(() -> {
                String tempMax;
                String tempMin;
                // Добавляем данные для текущего времени
                addCurrTimeDataToDataLists();
                addDataForNextFourDaysToDataLists();
                for (int i = 0; i < OpenWeatherMap.FORECAST_DAYS; i++) {
                    Log.d("WEATHER", "List Weather forcast size = " + weatherRequest.getList().size());
                    String degrees = degreesArrayList.get(i);
                    String windInfo = windInfoArrayList.get(i);
                    String pressure = pressureArrayList.get(i);
                    String weatherStateInfo = weatherStateInfoArrayList.get(i);
                    String feelLike = feelLikeArrayList.get(i);
                    int weatherIcon = weatherIconsArrayList.get(i);
                    // Кладем в списоки tempMax и tempMin полученные выше результаты:
                    tempMax = fiveDaysTempMax.get(i);
                    tempMin = fourDayTempMin.get(i);
                    WeatherData weatherData = new WeatherData(resources, degrees, windInfo, pressure, weatherStateInfo, feelLike, weatherIcon, tempMax, tempMin);
                    weekWeatherData.add(weatherData);
                    Log.d("tempMaxMin from int= ", tempMax + " " + tempMin);
                }
            });
            weekData.start();
            Log.d("Threads", "getWeekWeatherData start");
            try {
                weekData.join();
                Log.d("Threads", "getWeekWeatherData joined");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return weekWeatherData;
        }
        return null;
    }

    private void addCurrTimeDataToDataLists(){
        // Для текущего дня устанавливаем нули, т.к. у нас недостаточно информации
        // для вычисления наиболшей и наименьшей температуры дня, т.к. входящие данные содержат инфомацию
        // только с текущего момента, а не с начала текущего дня
        fiveDaysTempMax.add("0");
        fourDayTempMin.add("0");
        // Текущие данные:
        weatherStateInfoArrayList.add(String.format(Locale.getDefault(), "%s", weatherRequest.getList().get(0).getWeather().get(0).getDescription()));
        weatherIconsArrayList.add(weatherRequest.getList().get(0).getWeather().get(0).getId());
        degreesArrayList.add(String.format(Locale.getDefault(), "%s", Math.round(weatherRequest.getList().get(0).getMain().getTemp())));
        windInfoArrayList.add(String.format(Locale.getDefault(), "%s", Math.round(weatherRequest.getList().get(0).getWind().getSpeed())));
        pressureArrayList.add(String.format(Locale.getDefault(), "%s", weatherRequest.getList().get(0).getMain().getPressure()));
        feelLikeArrayList.add(String.format(Locale.getDefault(), "%s", weatherRequest.getList().get(0).getMain().getFeelsLike()));
    }

    private void addDataForNextFourDaysToDataLists(){
        int days = 0;
        // Добавим данные для следующих 4 дней
        // Проходим весь список погоды (40 элементов, первая запись соответвует времени, предшествующему текущему времени суток)
        for (int i = 0; i < weatherRequest.getList().size(); i++) {
            String time = String.format(Locale.getDefault(),
                    "%s", weatherRequest.getList().get(i).getDtTxt()).substring(11, 16);
            // Ищем начало следующего дня и собираем информацию на 4 след. дня:
            if (time.equals("00:00") && days <= OpenWeatherMap.FORECAST_DAYS - 2) {
                ArrayList<Float> dayMaxTemp = new ArrayList<>();
                ArrayList<Float> dayMinTemp = new ArrayList<>();
                // 8 раз берем инф., т.к. прогноз выдается на каждые 3 часа (8 * 3 = 24):
                for (int j = i; j < i + 8; j++) {
                    dayMaxTemp.add(weatherRequest.getList().get(j).getMain().getTempMax());
                    dayMinTemp.add(weatherRequest.getList().get(j).getMain().getTempMin());
                }
                // Считаем самую теплую и самую холодную температуру в течение одного дня:
                float currDayMaxTemp = Collections.max(dayMaxTemp);
                float currDayMinTemp = Collections.min(dayMinTemp);
                days++;
                fiveDaysTempMax.add(String.format(Locale.getDefault(), "%s", currDayMaxTemp));
                fourDayTempMin.add(String.format(Locale.getDefault(), "%s", currDayMinTemp));

                // Забираем информацию, соответсвующую 12:00, поэтому индекс = i + 4 (00.00->03.00->06.00->09.00->12.00)
                weatherStateInfoArrayList.add(String.format(Locale.getDefault(), "%s", weatherRequest.getList().get(i + 4).getWeather().get(0).getDescription()));
                weatherIconsArrayList.add(weatherRequest.getList().get(i + 4).getWeather().get(0).getId());
                degreesArrayList.add(String.format(Locale.getDefault(), "%s", Math.round(weatherRequest.getList().get(i + 4).getMain().getTemp())));
                windInfoArrayList.add(String.format(Locale.getDefault(), "%s", Math.round(weatherRequest.getList().get(i + 4).getWind().getSpeed())));
                pressureArrayList.add(String.format(Locale.getDefault(), "%s", weatherRequest.getList().get(i + 4).getMain().getPressure()));
                feelLikeArrayList.add(String.format(Locale.getDefault(), "%s", weatherRequest.getList().get(i + 4).getMain().getFeelsLike()));
            }
        }
    }

    public ArrayList<HourlyWeatherData> getHourlyWeatherData() {
        ArrayList<HourlyWeatherData> hourlyWeatherData = new ArrayList<>();
        weatherRequest = ForecastRequest.getWeatherRequest();
        if (weatherRequest != null) {
            Thread dayData = new Thread(() -> {
                for (int i = 1; i < 9; i++) {
                    String temperature = String.format(Locale.getDefault(), "%s", Math.round(weatherRequest.getList().get(i).getMain().getTemp()));
                    int weatherId = weatherRequest.getList().get(i).getWeather().get(0).getId();
                    String time = String.format(Locale.getDefault(),
                            "%s", weatherRequest.getList().get(i).getDtTxt()).substring(11, 16);
                    HourlyWeatherData hourlyForecastData = new HourlyWeatherData(time, weatherId, temperature);
                    hourlyWeatherData.add(hourlyForecastData);
                }
            });
            dayData.start();
            Log.d("Threads", "getHourlyWeatherData start");

            try {
                dayData.join();
                Log.d("Threads", "getHourlyWeatherData joined");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return hourlyWeatherData;
        }
        return null;
    }
}



