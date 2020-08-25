package ru.geekbrains.gb_android_2;

import java.io.Serializable;
import java.util.ArrayList;

public class CurrentDataContainer implements Serializable {
    public String currCityName = "";
    boolean[] switchSettingsArray;
    ArrayList<WeatherData> weekWeatherData = new ArrayList<>();
    ArrayList<String> citiesList = new ArrayList<>();
    static boolean isFirstEnter = true;
    static boolean isNightModeOn;
    static boolean NightIsAlreadySettedInMain;
    static boolean NightIsAlreadySettedInChoose;
}
