package com.jduttweiler.forecastalerts.service;

import java.util.List;

import com.jduttweiler.forecastalerts.dto.Forecast;
import com.jduttweiler.forecastalerts.dto.Location;

abstract public class ForecastService {
    abstract public Forecast getForecast(int days, Location location);
}
