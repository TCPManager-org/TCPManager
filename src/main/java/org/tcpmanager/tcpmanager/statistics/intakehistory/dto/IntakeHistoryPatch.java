package org.tcpmanager.tcpmanager.statistics.intakehistory.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public record IntakeHistoryPatch(@Min(1) Integer calories,
                                 @DecimalMin(value = "0", inclusive = false) BigDecimal protein,
                                 @DecimalMin(value = "0", inclusive = false) BigDecimal fat,
                                 @DecimalMin(value = "0", inclusive = false) BigDecimal carbs,
                                 @Min(value = 1) Integer caloriesGoal,
                                 @Min(value = 1) Integer proteinGoal,
                                 @Min(value = 1) Integer fatGoal,
                                 @Min(value = 1) Integer carbsGoal) {

}
