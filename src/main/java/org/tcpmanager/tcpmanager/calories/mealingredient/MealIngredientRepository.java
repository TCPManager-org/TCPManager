package org.tcpmanager.tcpmanager.calories.mealingredient;

import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.tcpmanager.tcpmanager.calories.meal.Meal;

public interface MealIngredientRepository extends
    JpaRepository<@NonNull MealIngredient, @NonNull Long> {

  List<MealIngredient> findAllByIngredientName(String ingredientName);
}
