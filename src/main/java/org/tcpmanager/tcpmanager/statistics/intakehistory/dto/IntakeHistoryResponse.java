package org.tcpmanager.tcpmanager.statistics.intakehistory.dto;

import java.math.BigDecimal;
import java.sql.Date;

public record IntakeHistoryResponse(Long id, Date date, Integer calories, BigDecimal protein,
                                    BigDecimal fat, BigDecimal carbs, Integer caloriesGoal,
                                    Integer proteinGoal, Integer fatGoal, Integer carbsGoal,
                                    String username) {

}
