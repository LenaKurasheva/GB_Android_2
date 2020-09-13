package ru.geekbrains.gb_android_2.model;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;

import ru.geekbrains.gb_android_2.R;


public class WeatherData implements Serializable {
    private String degrees;
    private String windInfo;
    private String pressure;
    private String weatherStateInfo;
    private String feelLike;
    private String weatherIcon;
    private String tempMax;
    private String tempMin;
    private int intDegrees;
    private Integer cardViewColor;

    int tempRandom;
    int windRandom;
    int pressureRandom;

    public String getDegrees(){return degrees;}
    public String getWindInfo(){return windInfo;}
    public String getPressure(){return pressure;}
    public String getWeatherStateInfo(){return weatherStateInfo;}
    public String getFeelLike(){return feelLike;}
    public String getWeatherIcon(){return weatherIcon;}
    public String getTempMax(){return tempMax;}
    public String getTempMin(){return tempMin;}
    public int getIntDegrees(){return intDegrees;}
    public int getCardViewColor(){return cardViewColor;}

    public WeatherData(Resources resources, String degrees, String windInfo, String pressure,
                       String weatherStateInfo, String feelLike, int weatherIcon, String tempMax, String tempMin){
        intDegrees = (int) Float.parseFloat(degrees.trim());
        this.degrees = prepareDegreesDisplay(degrees);
        this.tempMax = prepareDegreesDisplay(tempMax);
        Log.d("tempMax in WeatherData", this.tempMax);
        this.tempMin = prepareDegreesDisplay(tempMin);
        Log.d("tempMIN in WeatherData", this.tempMin);


        String windInfoFromRes = resources.getString(R.string.windInfo);
        this.windInfo = String.format(windInfoFromRes, windInfo);

        String pressureInfoFromRes = resources.getString(R.string.pressureInfo);
        float p = Float.parseFloat(pressure.trim());
        float pressureHpaToMmHgDivider = 1.33322387415f;
        float pressureInMmHg = p / pressureHpaToMmHgDivider;
        String stringPressure = String.valueOf(Math.round(pressureInMmHg));
        this.pressure = String.format(pressureInfoFromRes, stringPressure);

        this.weatherStateInfo = weatherStateInfo;

        String feelsLikeInfoFromRes = resources.getString(R.string.feels_like_temp);
        String sign;
        float f = Float.parseFloat(feelLike.trim());
        Log.d("myLog", "FeelsLike float from internet = " + f);
        if(f > 0) {sign = "+";} else {sign = "";}
        String stringFeelLike = String.valueOf(Math.round(f));
        this.feelLike = String.format(feelsLikeInfoFromRes, sign, stringFeelLike);

        findIconById(weatherIcon);
    }

    private String prepareDegreesDisplay(String degrees){
        String tempSign;
        float t = Float.parseFloat(degrees.trim());
        if(t > 0) {tempSign = "+";} else {tempSign = "";}
        return tempSign + roundDegreesValue(t) + "°";
    }

    private String roundDegreesValue(float degrees){
        String deg =  String.valueOf(Math.round(degrees));
        Log.d("tempM after roundDEgree", deg);
        return deg;
    }

    // Контруктор для создания рандомных данных погоды:
    public WeatherData(Resources resources){
        calculateRandomValues();
        degrees = "+" + tempRandom + "°";

        String feelsLikeInfoFromRes = resources.getString(R.string.feels_like_temp);
        feelLike = String.format(feelsLikeInfoFromRes, "+", (String.valueOf(tempRandom - 2)));

        String pressureInfoFromRes = resources.getString(R.string.pressureInfo);
        pressure = String.format(pressureInfoFromRes, String.valueOf(pressureRandom));

        String windInfoFromRes = resources.getString(R.string.windInfo);
        windInfo = String.format(windInfoFromRes, String.valueOf(windRandom));

        String[] weatherStateInfoFromRes = resources.getStringArray(R.array.weatherState);
        int weatherStateIndex = (int)(Math.random() * weatherStateInfoFromRes.length);
        weatherStateInfo = weatherStateInfoFromRes[weatherStateIndex];

        String[] weatherIconsFromRes = resources.getStringArray(R.array.iconsId);
        weatherIcon = weatherIconsFromRes[weatherStateIndex];
    }

    public WeatherData(){}

    @NonNull
    @Override
    public String toString() {
        return " - WEATHER DATA: degrees = " + degrees +
                " windInfo = " + windInfo +
                " pressure = " + pressure +
                " weatherStateInfo = " + weatherStateInfo +
                " feelLike = " + feelLike +
                " weatherIcon = " + weatherIcon +
                "tempMax = " + tempMax +
                "tempMin = " + tempMin +
                " cardColor = " + cardViewColor.toString();
    }

    public void findIconById(int weatherIcon){
        if(weatherIcon >= 200 && weatherIcon <= 232){
            this.weatherIcon = "thunderstorm";
            cardViewColor = R.color.weather_status_thunderstorm;
            return;
        }
        if(weatherIcon >= 300 && weatherIcon <= 321){
            this.weatherIcon = "shower_rain";
            cardViewColor = R.color.weather_status_shower_rain;
            return;

        }
        if(weatherIcon >= 500 && weatherIcon <= 531){
            this.weatherIcon = "rain_day";
            cardViewColor = R.color.weather_status_rain_day;
            return;

        }
        if(weatherIcon >= 600 && weatherIcon <= 622){
            this.weatherIcon = "snow";
            cardViewColor = R.color.weather_status_snow;
            return;

        }
        if(weatherIcon >= 700 && weatherIcon <= 781){
            this.weatherIcon = "mist";
            cardViewColor = R.color.weather_status_mist;
            return;

        }
        if(weatherIcon == 800){
            this.weatherIcon = "clear_sky_day";
            cardViewColor = R.color.weather_status_clear_sky_day;
            return;

        }
        if(weatherIcon == 801){
            this.weatherIcon = "few_clouds_day";
            cardViewColor = R.color.weather_status_few_clouds_day;
            return;

        }
        if(weatherIcon == 802){
            this.weatherIcon = "scattered_clouds";
            cardViewColor = R.color.weather_status_scattered_clouds;
            return;

        }
        if(weatherIcon == 803 || weatherIcon == 804 ){
            this.weatherIcon = "broken_clouds";
            cardViewColor = R.color.weather_status_broken_clouds;

        }
    }

    private void calculateRandomValues(){
        tempRandom = (int)(8 + Math.random() * 10);
        windRandom = (int)(Math.random() * 10);
        pressureRandom = 700 + (int)(Math.random() * 50);
    }
}
