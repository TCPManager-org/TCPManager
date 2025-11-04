package org.tcpmanager.tcpmanager.intakehistory.dto;

import java.math.BigDecimal;
import java.sql.Date;

public record IntakeHistoryResponse(Date date, BigDecimal calories, BigDecimal protein,
                                    BigDecimal fat, BigDecimal carbs, Integer caloriesGoal,
                                    Integer proteinGoal, Integer fatGoal, Integer carbsGoal,
                                    String username) {

}
