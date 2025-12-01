package org.tcpmanager.tcpmanager.calories.day.dto;

import java.sql.Date;
import java.util.List;


public record DayResponse(Long id, Date date, List<DayMealResponse> dayMeals) {

}
