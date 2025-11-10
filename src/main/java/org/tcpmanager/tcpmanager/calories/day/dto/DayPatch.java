package org.tcpmanager.tcpmanager.calories.day.dto;

import java.sql.Date;
import java.util.Set;

public record DayPatch(Date date, Long userId, Set<DayMealPatch> dayMeals) {

}
