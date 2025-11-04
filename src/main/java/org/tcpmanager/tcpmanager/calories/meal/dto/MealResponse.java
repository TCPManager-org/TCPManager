package org.tcpmanager.tcpmanager.calories.meal.dto;


import java.math.BigDecimal;
import java.util.Map;

public record MealResponse(Long id, String name, Boolean favorite, BigDecimal calories, BigDecimal fats,
                           BigDecimal carbs, BigDecimal proteins, Map<Long, String> ingredients) {

}
