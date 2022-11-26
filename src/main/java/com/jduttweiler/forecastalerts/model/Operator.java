package com.jduttweiler.forecastalerts.model;

public enum Operator {
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    EQUAL_TO("=="),
    GREATHER_THAN(">"),
    GREATHER_THAN_OR_EQUAL(">=");
  
    private String op;
  
    private Operator(String op){
      this.op = op;
    }
  
    public String getOp(){
      return this.op;
    }
  
    public String toString(){
      return this.op;
    }
  
  
  }