package com.example.l.myweather.util;

/**
 * Created by L on 2016-07-07.
 */
public class City {
    private String cityName;
    private String cityId;
    private String cityWeather;
    private String cityTemp;

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityTemp(String cityTemp) {
        this.cityTemp = cityTemp;
    }

    public void setCityWeather(String cityWeather) {
        this.cityWeather = cityWeather;
    }

    public String getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCityTemp() {
        return cityTemp;
    }

    public String getCityWeather() {
        return cityWeather;
    }

}
