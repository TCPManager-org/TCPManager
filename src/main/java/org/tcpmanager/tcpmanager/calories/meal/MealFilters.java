package org.tcpmanager.tcpmanager.calories.meal;

import java.math.BigDecimal;

public record MealFilters(Integer minIngredients,
                          Integer maxIngredients, BigDecimal minProteins, BigDecimal maxProteins,
                          BigDecimal minCarbs,
                          BigDecimal maxCarbs, BigDecimal minFats, BigDecimal maxFats,
                          Integer minCalories, Integer maxCalories) {

}
