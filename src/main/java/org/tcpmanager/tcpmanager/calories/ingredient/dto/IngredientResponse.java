package org.tcpmanager.tcpmanager.calories.ingredient.dto;

import java.math.BigDecimal;

public record IngredientResponse(Long id, String name, BigDecimal calories, BigDecimal fats,
                                 BigDecimal carbs, BigDecimal proteins, String ean) {

}
