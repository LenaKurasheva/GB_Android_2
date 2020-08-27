package ru.geekbrains.gb_android_2;

public class HourlyWeatherData {
    private String time;
    private String stateImage;
    private String temperature;

    public HourlyWeatherData(String time, int weatherId, String temperature) {
        this.time = time;

        WeatherData weatherData = new WeatherData();
        this.stateImage = weatherData.findIconById(weatherId);

        this.temperature = temperature;
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
