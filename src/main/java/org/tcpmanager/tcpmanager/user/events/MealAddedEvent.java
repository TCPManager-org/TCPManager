package org.tcpmanager.tcpmanager.user.events;

import java.math.BigDecimal;
import java.sql.Date;
import org.springframework.modulith.NamedInterface;

@NamedInterface
public record MealAddedEvent(Date date, String username, Integer calories, BigDecimal protein,
                             BigDecimal fat, BigDecimal carbs) {

}
