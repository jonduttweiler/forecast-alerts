package com.jduttweiler.forecastalerts;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

		String latitude = "-31.91274985363325";
		String longitude = "-61.400151850117595";

		OpenWeatherMapForecast forecast = ows.getForecast(7, new Location(latitude, longitude));

		checkOpenWeatherForecast(forecast, rules);

	}

	/*
	 * private Map<Integer, List<Predicate<Double>>>
	 * getPredicatesFromRules(List<WeatherRule> rules){
	 * PredicateFactory predicateFactory = new PredicateFactory();
	 * HashMap<Integer, List<Predicate<Double>>> predicates = new HashMap<>();
	 * 
	 * // Crear los predicados aca x fuera del bucle del forecast daily
	 * rules.forEach(rule -> {
	 * List<Predicate<Double>> rulePredicates = new ArrayList<>();
	 * List<WeatherCondition> conditions = rule.getConditions();
	 * // Obtener el predicado para la condicion y ver contra que variable va
	 * 
	 * for (WeatherCondition condition : conditions) {
	 * // Make some with condition
	 * String operator = condition.getOperator().toString();
	 * Predicate<Double> p = predicateFactory.build(operator,
	 * condition.getThreshold());
	 * rulePredicates.add(p);
	 * }
	 * predicates.put(rule.getId(), rulePredicates);
	 * });
	 * return predicates;
	 * }
	 * 
	 */

	private void checkOpenWeatherForecast(OpenWeatherMapForecast forecast, List<WeatherRule> rules) {

		forecast.daily.forEach(daily -> {
			System.out.println(Instant.ofEpochSecond(daily.dt));
			// check all rules against daily forecast
			for (WeatherRule rule : rules) {
				List<WeatherCondition> conditions = rule.getConditions();
				for(WeatherCondition condition : conditions){

					Predicate<Double> p = condition.buildPredicate();
					String vPath = condition.getVariable(); //Variable path maybe should be a good idea to handle it externally
					
					Double forecastValue =  daily.getValue(vPath);
					boolean conditionMatch = p.test(forecastValue);
					if(conditionMatch){
						System.out.println("Condition "+condition.getVariable()+" "+condition.getOperator().toString()+" "+condition.getThreshold() +" MATCH - forecast value "+ forecastValue);
						
					}

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

		String latitude = "-31.91274985363325";
		String longitude = "-61.400151850117595";

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
