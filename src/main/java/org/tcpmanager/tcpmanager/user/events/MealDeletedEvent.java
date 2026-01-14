package org.tcpmanager.tcpmanager.user.events;

import java.math.BigDecimal;
import java.sql.Date;
import org.springframework.modulith.NamedInterface;

@NamedInterface
public record MealDeletedEvent(Date date, String username, BigDecimal calories, BigDecimal protein,
                               BigDecimal fat, BigDecimal carbs) {

}
