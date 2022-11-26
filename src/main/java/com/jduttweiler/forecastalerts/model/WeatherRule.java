package com.jduttweiler.forecastalerts.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class WeatherRule {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id; 

    @Column(name="name")
    private String name;

    @OneToMany(mappedBy = "rule", orphanRemoval = true, cascade = CascadeType.ALL)
    List<WeatherCondition> conditions;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WeatherCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<WeatherCondition> conditions) {
        this.conditions = conditions;
    }


    public String toString(){
        String result =  "{ id: "+this.id+", name: "+this.name+" ";

        if(this.conditions.size()>0){
            String condsStr = "";
            for(WeatherCondition condition: this.conditions){
                condsStr += condition.getVariable()+" "+ condition.getOperator().toString()+ " "+ condition.getThreshold()+" "; 
            }

            result+=", conditions: "+ condsStr;
        }


        result +="}";

        return result;



    }

}
