package org.tcpmanager.tcpmanager.calories.mealingredient;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tcpmanager.tcpmanager.calories.meal.Meal;
import org.tcpmanager.tcpmanager.calories.models.Ingredient;

@Entity
@Data
@NoArgsConstructor
@Table(name = "meal_ingredients", schema = "calories")
public class MealIngredient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "meal_id", nullable = false)
  private Meal meal;

  @ManyToOne
  @JoinColumn(name = "ingredient_id", nullable = false)
  private Ingredient ingredient;

  private BigDecimal amount;
}
