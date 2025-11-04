package org.tcpmanager.tcpmanager.calories.meal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record MealRequest(@NotBlank String name, @NotNull Boolean favorite,
                          @NotNull @Size(min = 1) Map<Long, Integer> ingredients) {

}
