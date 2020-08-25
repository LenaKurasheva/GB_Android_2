package ru.geekbrains.gb_android_2;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;


public class WeatherData implements Serializable {
    String degrees;
    String windInfo;
    String pressure;
    String weatherStateInfo;
    String feelLike;
    String weatherIcon;

    int tempRandom;
    int windRandom;
    int pressureRandom;

    public WeatherData(Resources resources, String degrees, String windInfo, String pressure, String weatherStateInfo, String feelLike, int weatherIcon){
        String tempSign;
        float t = Float.parseFloat(degrees.trim());
        Log.d("myLog", "Degrees float from internet = " + t);
        if(t > 0) {tempSign = "+";} else {tempSign = "";}
        String stringTemperature = String.valueOf(Math.round(t));
        this.degrees = tempSign + stringTemperature +  "°";

        String windInfoFromRes = resources.getString(R.string.windInfo);
        this.windInfo = String.format(windInfoFromRes, windInfo);

        String pressureInfoFromRes = resources.getString(R.string.pressureInfo);
        float p = Float.parseFloat(pressure.trim());
        float pressureHpaToMmHgDivider = 1.33322387415f;
        float pressureInMmHg = (float) (p / pressureHpaToMmHgDivider);
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

    @NonNull
    @Override
    public String toString() {
        return " - WEATHER DATA: degrees = " + degrees +
                " windInfo = " + windInfo +
                " pressure = " + pressure +
                " weatherStateInfo = " + weatherStateInfo +
                " feelLike = " + feelLike +
                " weatherIcon = " + weatherIcon;
    }

    private void findIconById(int weatherIcon){
        if(weatherIcon >= 200 && weatherIcon <= 232){
            this.weatherIcon = "thunderstorm";
        }
        if(weatherIcon >= 300 && weatherIcon <= 321){
            this.weatherIcon = "shower_rain";
        }
        if(weatherIcon >= 500 && weatherIcon <= 531){
            this.weatherIcon = "rain_day";
        }
        if(weatherIcon >= 600 && weatherIcon <= 622){
            this.weatherIcon = "snow";
        }
        if(weatherIcon >= 700 && weatherIcon <= 781){
            this.weatherIcon = "mist";
        }
        if(weatherIcon == 800){
            this.weatherIcon = "clear_sky_day";
        }
        if(weatherIcon == 801){
            this.weatherIcon = "few_clouds_day";
        }
        if(weatherIcon == 802){
            this.weatherIcon = "scattered_clouds";
        }
        if(weatherIcon == 803 || weatherIcon == 804 ){
            this.weatherIcon = "broken_clouds";
        }
    }

    private void calculateRandomValues(){
        tempRandom = (int)(8 + Math.random() * 10);
        windRandom = (int)(Math.random() * 10);
        pressureRandom = 700 + (int)(Math.random() * 50);
    }
}
