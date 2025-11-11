package org.tcpmanager.tcpmanager.calories.meal.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;

@Entity
@Data
@Table(name = "meals_ingredients", schema = "calories")
public class MealIngredient {

  @EmbeddedId
  private MealIngredientKey id;

  @ManyToOne
  @MapsId("mealId")
  @JoinColumn(name = "meal_id", nullable = false)
  private Meal meal;

  @ManyToOne
  @MapsId("ingredientId")
  @JoinColumn(name = "ingredient_id", nullable = false)
  private Ingredient ingredient;

  private Integer weight;
}
