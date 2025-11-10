package org.tcpmanager.tcpmanager.calories.day.dto;

import jakarta.validation.constraints.NotNull;
import java.sql.Date;
import java.util.Set;

public record DayRequest(@NotNull Date date, @NotNull Long userId, @NotNull Set<DayMealRequest> dayMeals) {

}
