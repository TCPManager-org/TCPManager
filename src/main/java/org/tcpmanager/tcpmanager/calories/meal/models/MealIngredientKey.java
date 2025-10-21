package org.tcpmanager.tcpmanager.calories.meal.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public record MealIngredientKey(@Column(name = "meal_id") Long mealId,
                                @Column(name = "ingredient_id") Long ingredientId) implements
    Serializable {

}
