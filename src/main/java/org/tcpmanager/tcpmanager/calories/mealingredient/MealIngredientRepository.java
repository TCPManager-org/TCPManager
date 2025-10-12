package org.tcpmanager.tcpmanager.calories.mealingredient;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealIngredientRepository extends
    JpaRepository<@NonNull MealIngredient, @NonNull Long> {

}
