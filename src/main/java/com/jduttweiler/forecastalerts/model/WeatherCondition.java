package com.jduttweiler.forecastalerts.model;

import java.util.function.Predicate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jduttweiler.forecastalerts.PredicateFactory;

@Entity
public class WeatherCondition {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id; 

    private String variable; //WeatherVariable

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private Operator operator; 

    private Double threshold;


	@ManyToOne
	@JoinColumn(name = "id_rule")
    private WeatherRule rule;

    public String getVariable() {
        return variable;
    }
    public void setVariable(String variable) {
        this.variable = variable;
    }
    public Operator getOperator() {
        return operator;
    }
    public void setOperator(Operator operator) {
        this.operator = operator;
    }
    public Double getThreshold() {
        return threshold;
    }
    public void setThreshold(int threshold) {
        this.threshold = (double) threshold;
    }
    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public WeatherRule getRule() {
        return rule;
    }
    public void setRule(WeatherRule rule) {
        this.rule = rule;
    }

    public Predicate<Double> buildPredicate(){
        PredicateFactory factory = new PredicateFactory();
        return factory.build(this.operator.toString(), this.threshold);
    }
    
}
