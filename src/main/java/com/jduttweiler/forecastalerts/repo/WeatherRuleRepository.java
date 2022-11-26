package com.jduttweiler.forecastalerts.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jduttweiler.forecastalerts.model.WeatherRule;

public interface WeatherRuleRepository extends JpaRepository<WeatherRule, Integer>{
    
}
