package com.jduttweiler.forecastalerts.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.jduttweiler.forecastalerts.dto.Location;
import com.jduttweiler.forecastalerts.service.ForecastService;
import com.jduttweiler.forecastalerts.service.impl.openweather.OpenWeatherMapForecast;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("openWeatherMapService")
public class OpenWeatherMapService extends ForecastService {

    @Value("${openweathermap.appId}")
    private String appId;

    private String getAppId() {
        return appId; // TODO: Implement round robin
    }

    @Override
    public OpenWeatherMapForecast getForecast(int days, Location location) {

        try {
            String exclude = "current,minutely,hourly,alerts";
            URL url;
            String urlPlaceholder = "https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&appid=%s&units=metric&lang=es&exclude=%s";
            String surl = String.format(urlPlaceholder, location.getLatitude(), location.getLongitude(),
                    this.getAppId(), exclude);

            url = new URL(surl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            String myJsonString = content.toString();
            ObjectMapper om = new ObjectMapper();
            OpenWeatherMapForecast forecast = om.readValue(myJsonString, OpenWeatherMapForecast.class);

            return forecast;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
