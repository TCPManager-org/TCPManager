package org.tcpmanager.tcpmanager.calories.day.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.tcpmanager.tcpmanager.calories.day.models.MealType;

public record DayMealRequest(@NotNull Long mealId, @NotNull @Size(min = 1) Integer weight, @NotNull MealType mealType) {

}
