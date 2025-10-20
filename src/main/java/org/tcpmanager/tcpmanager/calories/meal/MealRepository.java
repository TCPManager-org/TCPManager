package org.tcpmanager.tcpmanager.calories.meal;

import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.tcpmanager.tcpmanager.calories.meal.models.MealIngredient;
import org.tcpmanager.tcpmanager.calories.meal.models.MealIngredientKey;

public interface MealRepository extends JpaRepository<@NonNull MealIngredient, @NonNull MealIngredientKey> {

  Optional<MealIngredient> findByMealId(Long id);
}
