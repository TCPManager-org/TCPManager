package org.tcpmanager.tcpmanager.calories.day.dto;

import jakarta.validation.constraints.Min;
import org.tcpmanager.tcpmanager.calories.day.models.MealType;

public record DayMealPatch(Long mealId, @Min(1) Integer weight, MealType mealType) {

}
