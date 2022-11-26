package com.jduttweiler.forecastalerts.service.impl.openweather;

import java.util.ArrayList;

public class DailyForecast{
    public int dt;
    public int sunrise;
    public int sunset;
    public int moonrise;
    public int moonset;
    public double moon_phase;
    public Temperature temp;
    public FeelsLike feels_like;
    public int pressure;
    public int humidity;
    public double dew_point;
    public double wind_speed;
    public int wind_deg;
    public double wind_gust;
    public ArrayList<Weather> weather;
    public int clouds;
    public double pop;
    public double rain;
    public double uvi;



    public Double getValue(String key){
        switch(key){
            case "temp.max": {
                return temp.max;
            }
            case "temp.min": {
                return temp.min;
            }
            case "wind_speed": {
                return wind_speed;
            }
            case "uvi": {
                return uvi;
            }
            case "humidity": {
                return (double) humidity;
            }
            case "pressure": {
                return (double) pressure;
            }
        }




        return null;
    }


}



