package org.tcpmanager.tcpmanager.calories.ingredient.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record IngredientRequest(@NotBlank String name,
                                Long userId,
                                @NotNull @Min(1) Integer calories,
                                @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal fats,
                                @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal carbs,
                                @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal proteins,
                                @Size(min = 13, max = 13, message = "must be 13 characters long") String ean) {

}
