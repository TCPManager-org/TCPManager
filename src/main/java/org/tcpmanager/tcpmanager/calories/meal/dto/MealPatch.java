package org.tcpmanager.tcpmanager.calories.meal.dto;

import jakarta.validation.constraints.Size;
import java.util.Map;

public record MealPatch(String name,
                        @Size(min = 1) Map<Long, Integer> ingredients) {

}
