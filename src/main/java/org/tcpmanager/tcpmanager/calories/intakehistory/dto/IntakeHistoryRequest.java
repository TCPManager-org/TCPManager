package org.tcpmanager.tcpmanager.calories.intakehistory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Date;

public record IntakeHistoryRequest(@NotNull Date date,
                                   @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal calories,
                                   @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal protein,
                                   @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal fat,
                                   @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal carbs,
                                   @NotNull @Min(value = 1) Integer caloriesGoal,
                                   @NotNull @Min(value = 1) Integer proteinGoal,
                                   @NotNull @Min(value = 1) Integer fatGoal,
                                   @NotNull @Min(value = 1) Integer carbsGoal,
                                   @NotBlank String username) {

}
