package com.jduttweiler.forecastalerts;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.function.Predicate;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jduttweiler.forecastalerts.dto.Location;
import com.jduttweiler.forecastalerts.model.WeatherCondition;
import com.jduttweiler.forecastalerts.model.WeatherRule;
import com.jduttweiler.forecastalerts.repo.WeatherRuleRepository;
import com.jduttweiler.forecastalerts.service.impl.OpenWeatherMapService;
import com.jduttweiler.forecastalerts.service.impl.openweather.OpenWeatherMapForecast;

@SpringBootApplication
@RestController
public class ForecastAlertsApplication implements CommandLineRunner {
	private static Logger LOG = org.slf4j.LoggerFactory.getLogger(ForecastAlertsApplication.class);

	@Autowired
	private OpenWeatherMapService ows;

	@Autowired
	private WeatherRuleRepository repo;

	public static void main(String[] args) {
		SpringApplication.run(ForecastAlertsApplication.class, args);
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		LOG.info("App is running");

		List<WeatherRule> rules = repo.findAll();

		Location l1 = new Location("Colonia Belgrano", -31.91274985363325, -61.400151850117595);
		Location l2 = new Location("Sauzalito", 	-24.429471054710707, -61.68369052591683);
		Location l3 = new Location("Santa Victoria Este",-22.272539858681256, -62.712726537319696);
		Location l4 = new Location("Filadelfia", -22.308442674644184, -60.083253973322854);
		Location l5 = new Location("Tarija", -21.52028734274685, -64.72766254591924);
		Location l6 = new Location("Villa Montes ", -21.2600046, -63.4580702);
		Location l7 = new Location("Yacuiba ", -22.0227557, -63.6775234);
		Location l8 = new Location("General Enrique Mosconi", -23.2166691, -62.2999992);
			 	

		List<Location> locations = new ArrayList<>(Arrays.asList(l1,l2,l3,l4,l5, l6, l7, l8));
		locations.stream().forEach(location -> {
			System.out.println("--------------");
			System.out.println(location.getName());
			OpenWeatherMapForecast forecast = ows.getForecast(7, location);
			checkOpenWeatherForecast(forecast, rules);
		});
		




	}

	private void checkOpenWeatherForecast(OpenWeatherMapForecast forecast, List<WeatherRule> rules) {

		forecast.daily.forEach(daily -> {
			System.out.println(Instant.ofEpochSecond(daily.dt));
			// check all rules against daily forecast
			for (WeatherRule rule : rules) {
				List<WeatherCondition> conditions = rule.getConditions();

				boolean ruleMatch = conditions.stream().allMatch(condition -> {
					Predicate<Double> p = condition.buildPredicate();
					String vPath = condition.getVariable(); //Variable path maybe should be a good idea to handle it externally
										
					Double forecastValue =  daily.getValue(vPath); 
					System.out.println(vPath+": "+forecastValue);
					return p.test(forecastValue);
				});

				if(ruleMatch){
					System.out.println("Rule ["+rule.getName()+"] match");
				}
			}

			System.out.println("");
		});

	}

	private void checkForecast() {

		Double tMax = 30d;
		// Double wMax = 22.5d;/* Bee speed is 22.5km/h */
		Double wMax = 22.5d;
		Double sMax = 8d;

		PredicateFactory predicateFactory = new PredicateFactory();
		Predicate<Double> hotTemperature = predicateFactory.build(">", tMax);
		Predicate<Double> windy = predicateFactory.build(">", wMax);
		Predicate<Double> highSolarRadiation = predicateFactory.build(">", sMax);

		// Put predicates in array of rules

		double latitude = -31.91274985363325;
		double longitude = -61.400151850117595;

		OpenWeatherMapForecast forecast = ows.getForecast(7, new Location(latitude, longitude));

		forecast.daily.forEach(daily -> {

			boolean isHot = hotTemperature.test(daily.getValue("temp.max"));
			boolean isWindy = windy.test(daily.getValue("wind_speed"));
			boolean isSunny = highSolarRadiation.test(daily.getValue("uvi")); // El valor, se que es una funcion
																					// que retorna algo del mismo tipo
																					// que el predicado

			if (isHot) {
				System.out.println("Temperature alert for the day " + Instant.ofEpochSecond(daily.dt) + " - temp max: "
						+ daily.temp.max + " over threshold of " + tMax + "°C"); // quizas debamos tener un counter para
																					// ver que la condicion

			}
			if (isWindy) {
				System.out.println("Windy alert for the day " + Instant.ofEpochSecond(daily.dt) + " - wind speed: "
						+ daily.wind_speed + " over threshold of " + wMax + " km/h");
			}

			if (isSunny) {
				System.out.println("Uv alert for the day " + Instant.ofEpochSecond(daily.dt) + " - UV index: "
						+ daily.uvi + " over threshold of " + sMax);
			}

		});

	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

}
