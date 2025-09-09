package org.tcpmanager.tcpmanager.calories.ingredient.dto;

import java.math.BigDecimal;

public record IngredientResponse(Long id, String name, BigDecimal calories, BigDecimal fat, BigDecimal carbs, BigDecimal protein, String ean) {

}
