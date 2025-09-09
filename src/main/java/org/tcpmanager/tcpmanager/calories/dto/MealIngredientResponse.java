package org.tcpmanager.tcpmanager.calories.dto;

import java.math.BigDecimal;
import org.tcpmanager.tcpmanager.calories.meal.MealResponse;

public record MealIngredientResponse(Long id, MealResponse meal, IngredientResponse ingredient, BigDecimal amount) {

}
