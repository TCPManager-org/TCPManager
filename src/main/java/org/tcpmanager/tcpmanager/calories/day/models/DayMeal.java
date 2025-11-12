package org.tcpmanager.tcpmanager.calories.day.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tcpmanager.tcpmanager.calories.meal.models.Meal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "day_meal", schema = "calories")
public class DayMeal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  private Integer weight;

  @Enumerated(EnumType.STRING)
  @Column(name = "meal_type", nullable = false, length = 20)
  private MealType mealType;

  @ManyToOne
  @JoinColumn(name = "day_id", nullable = false)
  private Day day;

  @ManyToOne
  @JoinColumn(name = "meal_id", nullable = false)
  private Meal meal;
}
