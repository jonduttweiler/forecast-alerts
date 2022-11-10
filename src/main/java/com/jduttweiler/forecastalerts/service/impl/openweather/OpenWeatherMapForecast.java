package com.jduttweiler.forecastalerts.service.impl.openweather;

import java.util.ArrayList;

import com.jduttweiler.forecastalerts.dto.Forecast;

public class OpenWeatherMapForecast extends Forecast{
    public double lat;
    public double lon;
    public String timezone;
    public int timezone_offset;
    public ArrayList<DailyForecast> daily;
}
