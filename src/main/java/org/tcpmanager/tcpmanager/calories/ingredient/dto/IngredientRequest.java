package org.tcpmanager.tcpmanager.calories.ingredient.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record IngredientRequest(@NotBlank String name,
                                @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal calories,
                                @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal fats,
                                @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal carbs,
                                @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal proteins,
                                String ean) {

}
