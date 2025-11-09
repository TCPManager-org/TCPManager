package org.tcpmanager.tcpmanager.calories.day.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Date;

@Embeddable
public record DayMealKey(@Column(name = "day_date") Date dayDate, @Column(name = "meal_id") Long mealId) implements
    Serializable {

}
