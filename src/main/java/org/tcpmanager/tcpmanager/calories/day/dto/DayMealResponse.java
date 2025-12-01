package org.tcpmanager.tcpmanager.calories.day.dto;

import org.tcpmanager.tcpmanager.calories.day.models.MealType;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealResponse;

public record DayMealResponse(Long id, Integer weight, MealType mealType, MealResponse meal) {

}
