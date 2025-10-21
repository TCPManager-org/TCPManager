package org.tcpmanager.tcpmanager.calories.meal.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "meals_ingredients", schema = "calories")
public class MealIngredient {

  @EmbeddedId
  private MealIngredientKey id;

  @ManyToOne
  @MapsId("mealId")
  @JoinColumn(name = "meal_id")
  private Meal meal;

  @ManyToOne
  @MapsId("ingredientId")
  @JoinColumn(name = "ingredient_id")
  private Ingredient ingredient;

  private Integer weight;
}
