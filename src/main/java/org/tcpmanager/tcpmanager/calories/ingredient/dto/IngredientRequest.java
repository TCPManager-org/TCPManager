package org.tcpmanager.tcpmanager.calories.ingredient.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record IngredientRequest(@NotBlank String name,
                                @DecimalMin(value = "0.0", inclusive = false) BigDecimal calories,
                                @DecimalMin(value = "0.0", inclusive = false) BigDecimal fat,
                                @DecimalMin(value = "0.0", inclusive = false) BigDecimal carbs,
                                @DecimalMin(value = "0.0", inclusive = false) BigDecimal protein,
                                String ean) {

}
