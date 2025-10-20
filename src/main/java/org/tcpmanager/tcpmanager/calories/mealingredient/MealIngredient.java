package org.tcpmanager.tcpmanager.calories.mealingredient;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;
import org.tcpmanager.tcpmanager.calories.meal.Meal;

@Entity
@Data
@NoArgsConstructor
@Table(name = "meal_ingredients", schema = "calories")
public class MealIngredient {

  @EmbeddedId
  private MealIngredientKey id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "meal_id")
  private Meal meal;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "ingredient_id")
  private Ingredient ingredient;

  private BigDecimal amount;
}
