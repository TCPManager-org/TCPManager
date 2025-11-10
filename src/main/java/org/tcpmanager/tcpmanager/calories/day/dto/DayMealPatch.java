package org.tcpmanager.tcpmanager.calories.day.dto;

import jakarta.validation.constraints.Size;
import org.tcpmanager.tcpmanager.calories.day.models.MealType;

public record DayMealPatch(Long mealId, @Size(min = 1) Integer weight, MealType mealType) {

}
