package org.tcpmanager.tcpmanager.calories.meal;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealResponse;
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

  public MealResponse getById(Long id) {
    MealIngredient mealIngredient =  mealRepository.findByMealId(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    return mapToMealResponse(mealIngredient);
  }

  private MealResponse mapToMealResponse(@NonNull MealIngredient meal) {
    BigDecimal calories = BigDecimal.ZERO;
    BigDecimal carbs = BigDecimal.ZERO;
    BigDecimal fats = BigDecimal.ZERO;
    BigDecimal proteins = BigDecimal.ZERO;
    Map<Long, String> ingredients = new HashMap<>();
    for (var mealIngredient : meal.getMeal().getMealIngredients()) {
      Ingredient ingredient = mealIngredient.getIngredient();
      calories = calories.add(ingredient.getCalories());
      carbs = carbs.add(ingredient.getCarbs());
      fats = fats.add(ingredient.getFats());
      proteins = proteins.add(ingredient.getProteins());
      ingredients.put(ingredient.getId(), ingredient.getName());
    }
    return new MealResponse(meal.getId().mealId(), meal.getMeal().getName(), calories, carbs, fats,
        proteins, ingredients);
  }


}
