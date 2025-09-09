package org.tcpmanager.tcpmanager.calories.dto;

import java.math.BigDecimal;

public record MealIngredientResponse(Long id, MealResponse meal, IngredientResponse ingredient, BigDecimal amount) {

}
