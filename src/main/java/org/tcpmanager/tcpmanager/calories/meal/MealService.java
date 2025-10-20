package org.tcpmanager.tcpmanager.calories.meal;

import static org.springframework.data.util.Pair.toMap;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealResponse;
import org.tcpmanager.tcpmanager.calories.meal.models.Meal;
import org.tcpmanager.tcpmanager.calories.meal.models.MealIngredient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealService {

  private final MealRepository mealRepository;

  private static String generateNotFoundMessage(Long id) {
    return "Meal with id " + id + " not found";
  }

  public List<MealResponse> getAllMeals() {
    return mealRepository.findAll().stream().map(this::mapToMealResponse).toList();
  }

  private MealResponse mapToMealResponse(@NonNull MealIngredient meal) {
    BigDecimal calories = BigDecimal.ZERO;
    BigDecimal carbs = BigDecimal.ZERO;
    BigDecimal fats = BigDecimal.ZERO;
    BigDecimal proteins = BigDecimal.ZERO;
    Map<Long, String> ingredients = new HashMap<>();
    for (var ingredient : meal.getMeal().getIngredients()) {
      calories = calories.add(ingredient.getIngredient().getCalories());
      carbs = carbs.add(ingredient.getIngredient().getCarbs());
      fats = fats.add(ingredient.getIngredient().getFats());
      proteins = proteins.add(ingredient.getIngredient().getProteins());
      ingredients.put(ingredient.getIngredient().getId(), ingredient.getIngredient().getName());
    }
    return new MealResponse(meal.getId().mealId(), meal.getMeal().getName(), calories, carbs, fats, proteins, ingredients);
  }
}
