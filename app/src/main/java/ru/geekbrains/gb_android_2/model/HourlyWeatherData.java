package ru.geekbrains.gb_android_2.model;

import android.util.Log;

import java.io.Serializable;

import ru.geekbrains.gb_android_2.model.WeatherData;

public class HourlyWeatherData implements Serializable {
    private String time;
    private String stateImage;
    private String temperature;

    public HourlyWeatherData(String time, int weatherId, String temperature) {
        this.time = time;

        WeatherData weatherData = new WeatherData();
        this.stateImage = weatherData.findIconById(weatherId);


        String tempSign = "";
        float t = Float.parseFloat(temperature.trim());
        Log.d("myLog", "Degrees float from internet = " + t);
        if(t > 0) {temperature = "+";} else {tempSign = "";}
        String stringTemperature = String.valueOf(Math.round(t));
        this.temperature = tempSign + stringTemperature +  "°";
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStateImage() {
        return stateImage;
    }

    public void setStateImage(String stateImage) {
        this.stateImage = stateImage;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
