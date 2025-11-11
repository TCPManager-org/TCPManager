package org.tcpmanager.tcpmanager.calories.day.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.sql.Date;
import org.tcpmanager.tcpmanager.calories.day.models.MealType;

public record DayMealRequest(@NotNull Date date, @NotNull Long userId, @NotNull Long mealId, @NotNull @Min(1) Integer weight, @NotNull MealType mealType) {

}
