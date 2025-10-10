package org.tcpmanager.tcpmanager.calories.ingredient.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.math.BigDecimal;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record IngredientRequest(String name, BigDecimal calories, BigDecimal fat, BigDecimal carbs,
                                BigDecimal protein, String ean) {

}
