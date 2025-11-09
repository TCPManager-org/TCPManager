package org.tcpmanager.tcpmanager.calories.day.dto;

import java.sql.Date;
import java.util.Map;

public record DayResponse(Date date, Map<Long, Integer>portionsByMealId) {

}
