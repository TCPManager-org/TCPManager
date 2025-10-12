package org.tcpmanager.tcpmanager.calories.meal.dto;

import jakarta.validation.constraints.NotBlank;

public record MealRequest(@NotBlank String name) {

}
