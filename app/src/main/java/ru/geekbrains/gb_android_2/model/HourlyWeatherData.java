package ru.geekbrains.gb_android_2.model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class HourlyWeatherData implements Serializable {
    private String time;
    private String stateImage;
    private String temperature;

    public HourlyWeatherData(String time, int weatherId, String temperature) {
        this.time = time;

        this.stateImage = findIconById(weatherId, time);

        String tempSign = "";
        float t = Float.parseFloat(temperature.trim());
        Log.d("myLog", "Degrees float from internet = " + t);
        if(t > 0) {temperature = "+";} else {tempSign = "";}
        String stringTemperature = String.valueOf(Math.round(t));
        this.temperature = tempSign + stringTemperature +  "°";
    }

    @NonNull
    @Override
    public String toString() {
        return "HourlyWeatherData: temperature = " + temperature + "stateImage = " + stateImage;

    }

    public String findIconById(int weatherIcon, String time){
        if(weatherIcon >= 200 && weatherIcon <= 232){
            this.stateImage = "thunderstorm";
            return this.stateImage;
        }
        if(weatherIcon >= 300 && weatherIcon <= 321){
            this.stateImage = "shower_rain";
            return this.stateImage;
        }
        if(weatherIcon >= 500 && weatherIcon <= 531){
            this.stateImage = "rain_day";
            return this.stateImage;
        }
        if(weatherIcon >= 600 && weatherIcon <= 622){
            this.stateImage = "snow";
            return this.stateImage;
        }
        if(weatherIcon >= 700 && weatherIcon <= 781){
            this.stateImage = "mist";
            return this.stateImage;
        }
        if(weatherIcon == 800){
            if(time.equals("00:00") || time.equals("03:00")){
                this.stateImage = "clear_sky_night";
                return this.stateImage;
            } else {
                this.stateImage = "clear_sky_day";
                return this.stateImage;
            }
        }
        if(weatherIcon == 801){
            if(time.equals("00:00") || time.equals("03:00")){
                this.stateImage = "few_clouds_night";
                return this.stateImage;
            } else {
                this.stateImage = "few_clouds_day";
                return this.stateImage;
            }
        }
        if(weatherIcon == 802){
            this.stateImage = "scattered_clouds";
            return this.stateImage;
        }
        if(weatherIcon == 803 || weatherIcon == 804 ){
            this.stateImage = "broken_clouds";
            return this.stateImage;
        }
        return null;
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
