package com.jduttweiler.forecastalerts;

import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jduttweiler.forecastalerts.dto.Location;
import com.jduttweiler.forecastalerts.service.ForecastService;
import com.jduttweiler.forecastalerts.service.impl.OpenWeatherMapService;
import com.jduttweiler.forecastalerts.service.impl.openweather.OpenWeatherMapForecast;

@SpringBootApplication
@RestController
public class ForecastAlertsApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ForecastAlertsApplication.class, args);


		Double tMax = 30d;
		//Double wMax = 22.5d;/* Bee speed is 22.5km/h */
		Double wMax = 5.5d;
		Double sMax = 8d;


		PredicateFactory predicateFactory = new PredicateFactory();
		Predicate<Double> hotTemperature = predicateFactory.build(">", tMax);
		Predicate<Double> windy = predicateFactory.build(">", wMax);
		Predicate<Double> highSolarRadiation = predicateFactory.build(">", sMax);
 

		// Put predicates in array of rules

		String latitude = "-31.91274985363325";
		String longitude = "-61.400151850117595";
		
	
		OpenWeatherMapService ows = context.getBean(OpenWeatherMapService.class);
 		OpenWeatherMapForecast forecast = ows.getForecast(7, new Location(latitude, longitude));

		forecast.daily.forEach(daily -> {

			if(hotTemperature.test(daily.temp.max)) {
				System.out.println("Temperature alert for the day "+Instant.ofEpochSecond(daily.dt)+" - temp max: "+daily.temp.max +" over threshold of "+tMax+"°C"); // quizas debamos tener un counter para ver que la condicion

			}
			if(windy.test(daily.wind_speed)){
				System.out.println("Windy alert for the day "+Instant.ofEpochSecond(daily.dt)+" - wind speed: "+daily.wind_speed+" over threshold of "+ wMax + " km/h");
			}

			if(highSolarRadiation.test(daily.uvi)){
				System.out.println("Uv alert for the day "+Instant.ofEpochSecond(daily.dt)+" - UV index: "+daily.uvi+" over threshold of "+ sMax );
			}



		}); 

	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

}
