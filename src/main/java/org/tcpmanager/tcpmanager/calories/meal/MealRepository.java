package org.tcpmanager.tcpmanager.calories.meal;

import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.tcpmanager.tcpmanager.calories.meal.models.Meal;

public interface MealRepository extends JpaRepository<@NonNull Meal, @NonNull Long> {

  Optional<Meal> findById(Long mealId);

  boolean existsById(Long mealId);

  void deleteById(Long mealId);
}
