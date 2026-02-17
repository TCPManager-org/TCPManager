package org.tcpmanager.tcpmanager.calories.ingredient.dto;

import java.math.BigDecimal;

public record IngredientResponse(Long id, String name, Integer calories, BigDecimal fats,
                                 BigDecimal carbs, BigDecimal proteins, String ean) {

}
