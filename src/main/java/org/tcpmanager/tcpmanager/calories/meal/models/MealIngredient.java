package org.tcpmanager.tcpmanager.calories.meal.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;

@Entity
@Data
@NoArgsConstructor
@Table(name = "meals_ingredients", schema = "calories")
public class MealIngredient {

  @EmbeddedId
  private MealIngredientKey id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("mealId")
  @JoinColumn(name = "meal_id")
  private Meal meal;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("ingredientId")
  @JoinColumn(name = "ingredient_id")
  private Ingredient ingredient;

  private Integer amount;
}
