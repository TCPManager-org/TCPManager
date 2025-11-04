package org.tcpmanager.tcpmanager.calories.meal.dto;

import jakarta.validation.constraints.Size;
import java.util.Map;

public record MealPatch(String name, Boolean favorite,
                        @Size(min = 1) Map<Long, Integer> ingredients) {

}
