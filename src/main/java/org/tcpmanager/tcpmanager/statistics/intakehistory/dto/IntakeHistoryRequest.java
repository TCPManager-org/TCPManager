package org.tcpmanager.tcpmanager.statistics.intakehistory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Date;

public record IntakeHistoryRequest(@NotNull Date date,
                                   @NotNull @DecimalMin(value = "0") BigDecimal calories,
                                   @NotNull @DecimalMin(value = "0") BigDecimal protein,
                                   @NotNull @DecimalMin(value = "0") BigDecimal fat,
                                   @NotNull @DecimalMin(value = "0") BigDecimal carbs,
                                   @NotNull @Min(value = 1) Integer caloriesGoal,
                                   @NotNull @Min(value = 1) Integer proteinGoal,
                                   @NotNull @Min(value = 1) Integer fatGoal,
                                   @NotNull @Min(value = 1) Integer carbsGoal) {

}
