package com.jduttweiler.forecastalerts;

import java.util.function.Predicate;


public class PredicateFactory {

    public <T extends Comparable<T>> Predicate<T> buildPredicate(String operator, T threshold) {

        return new Predicate<T>() {
            @Override
            public boolean test(T t) {

                switch(operator){
                    case "<" : {
                        return t.compareTo(threshold) < 0;
                    }
                    case "<=" : {
                        return t.compareTo(threshold) <= 0;
                    }
                    case "==" : {
                        return t.compareTo(threshold) == 0;
                    }
                    case ">=" : {
                        return t.compareTo(threshold) >= 0;
                    }
                    case ">" : {
                        return t.compareTo(threshold) > 0;
                    }
                    default: {
                        return false; //TODO: HANDLE EXCEPTIONS
                }
            }
        }
        };
    }

    

}
