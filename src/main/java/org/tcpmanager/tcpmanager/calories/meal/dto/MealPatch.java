package org.tcpmanager.tcpmanager.calories.meal.dto;

import jakarta.validation.constraints.NotBlank;

public record MealPatch(@NotBlank String name) {

}
