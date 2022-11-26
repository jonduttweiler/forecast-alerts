package com.jduttweiler.forecastalerts.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jduttweiler.forecastalerts.model.Operator;
import com.jduttweiler.forecastalerts.model.WeatherCondition;
import com.jduttweiler.forecastalerts.model.WeatherRule;
import com.jduttweiler.forecastalerts.repo.WeatherRuleRepository;


@RestController
public class WeatherRulesController {
    @Autowired
    private WeatherRuleRepository repo;

    @GetMapping("/weather/rules") 
    public String greeting() {

        WeatherRule rule = new WeatherRule();
        rule.setName(Instant.now().toString());


        List<WeatherCondition> conditions = new ArrayList<WeatherCondition>();  

        WeatherCondition hotDay = new WeatherCondition();
        hotDay.setVariable("temp.min");
        hotDay.setOperator(Operator.GREATHER_THAN_OR_EQUAL);
        hotDay.setThreshold(32);
        hotDay.setRule(rule);

        WeatherCondition wetDay = new WeatherCondition();
        wetDay.setVariable("humidity");
        wetDay.setOperator(Operator.LESS_THAN_OR_EQUAL);
        wetDay.setThreshold(80);
        wetDay.setRule(rule);


        conditions.add(hotDay);
        conditions.add(wetDay);

        rule.setConditions(conditions);
        repo.save(rule);

        String response = repo.findAll().stream().map(x -> x.getId() +" "+x.getName()).collect(Collectors.joining("<br>"));

        return response;
    }


/* Find by id */
    @GetMapping("/weather/rules/{id}") 
    public String getRuleById(@PathVariable String id){
        int iid = Integer.parseInt(id);

        Optional<WeatherRule> result = repo.findById(iid);
        if(result.isPresent()){
            WeatherRule rule = result.get();
            return rule.toString(); //Una regla x lo general va a tener varias condiciones
        } else {
            return "Record for id "+id+" Not found.";
        }
    }




}
