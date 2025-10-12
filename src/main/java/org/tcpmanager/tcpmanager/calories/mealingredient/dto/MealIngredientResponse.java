package org.tcpmanager.tcpmanager.calories.mealingredient.dto;

import java.math.BigDecimal;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientResponse;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealResponse;

public record MealIngredientResponse(Long id, MealResponse meal, IngredientResponse ingredient,
                                     BigDecimal amount) {

}
