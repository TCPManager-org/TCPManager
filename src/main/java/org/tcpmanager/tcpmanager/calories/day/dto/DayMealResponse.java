package org.tcpmanager.tcpmanager.calories.day.dto;

import org.tcpmanager.tcpmanager.calories.day.models.MealType;

public record DayMealResponse(Long mealId,Integer weight, MealType mealType) {

}
