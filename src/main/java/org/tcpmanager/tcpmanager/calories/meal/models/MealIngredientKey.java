package org.tcpmanager.tcpmanager.calories.meal.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import java.io.Serializable;

@Embeddable
@Table(name = "meals_ingredients", schema = "calories")
public record MealIngredientKey(@Column(name = "meal_id") Long mealId,
                                @Column(name = "ingredient_id") Long ingredientId) implements
    Serializable {

}
