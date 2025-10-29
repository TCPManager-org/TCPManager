package org.tcpmanager.tcpmanager.calories.ingredient.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record IngredientPatch(String name,
                              @DecimalMin(value = "0", inclusive = false) BigDecimal calories,
                              @DecimalMin(value = "0", inclusive = false) BigDecimal fats,
                              @DecimalMin(value = "0", inclusive = false) BigDecimal carbs,
                              @DecimalMin(value = "0", inclusive = false) BigDecimal proteins,
                              @Size(min = 13, max = 13, message = "must be 13 characters long") String ean) {

}
