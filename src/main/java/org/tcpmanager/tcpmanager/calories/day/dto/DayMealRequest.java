package org.tcpmanager.tcpmanager.calories.day.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Date;
import org.tcpmanager.tcpmanager.calories.day.models.MealType;

public record DayMealRequest(@NotNull Date date, @NotBlank String username,
                             @NotBlank String mealName, @NotNull @Min(1) Integer weight,
                             @NotNull MealType mealType) {

}
