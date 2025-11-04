package org.tcpmanager.tcpmanager.calories.day.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tcpmanager.tcpmanager.calories.meal.models.Meal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "days_meal", schema = "calories")
public class DayMeal {

  @EmbeddedId
  private DayMealKey id;

  @ManyToOne
  @MapsId("dayDate")
  @JoinColumn(name = "day_date", nullable = false)
  private Day day;

  @ManyToOne
  @MapsId("mealId")
  @JoinColumn(name = "meal_id", nullable = false)
  private Meal meal;

  private Integer weight;

  //TODO: add type of meal (breakfast, lunch, dinner)

}
