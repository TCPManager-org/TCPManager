package org.tcpmanager.tcpmanager.calories.day.dto;

import java.sql.Date;
import org.tcpmanager.tcpmanager.calories.day.models.MealType;

public record DayMealPatch(String mealName, Integer weight,
                           MealType mealType) {

}
