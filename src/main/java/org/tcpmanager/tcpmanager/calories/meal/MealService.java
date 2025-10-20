package org.tcpmanager.tcpmanager.calories.meal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;
import org.tcpmanager.tcpmanager.calories.ingredient.IngredientRepository;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealRequest;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealResponse;
import org.tcpmanager.tcpmanager.calories.meal.models.Meal;
import org.tcpmanager.tcpmanager.calories.meal.models.MealIngredient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealService {

  private final MealRepository mealRepository;
  private final IngredientRepository ingredientRepository;

  private static String generateNotFoundMessage(Long id) {
    return "Meal with id " + id + " not found";
  }

  public List<MealResponse> getAllMeals() {
    return mealRepository.findAll().stream().map(this::mapToMealResponse).toList();
  }

  public MealResponse getById(Long id) {
    Meal meal = mealRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    return mapToMealResponse(meal);
  }

  private MealResponse mapToMealResponse(@NonNull Meal meal) {
    BigDecimal calories = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    BigDecimal carbs = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    BigDecimal fats = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    BigDecimal proteins = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    Map<Long, String> ingredients = new HashMap<>();
    for (var mealIngredient : meal.getMealIngredients()) {
      Ingredient ingredient = mealIngredient.getIngredient();
      calories = calories.add(ingredient.getCalories().multiply(
          BigDecimal.valueOf(mealIngredient.getWeight())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN)));
      carbs = carbs.add(ingredient.getCarbs().multiply(
          BigDecimal.valueOf(mealIngredient.getWeight())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN)));
      fats = fats.add(ingredient.getFats().multiply(
          BigDecimal.valueOf(mealIngredient.getWeight())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN)));
      proteins = proteins.add(ingredient.getProteins().multiply(
          BigDecimal.valueOf(mealIngredient.getWeight())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN)));
      ingredients.put(ingredient.getId(), ingredient.getName());
    }
    return new MealResponse(meal.getId(), meal.getName(), calories, carbs, fats, proteins,
        ingredients);
  }

  @Transactional
  public MealResponse addMeal(@Valid MealRequest mealRequest) {
    Meal meal = new Meal();
    meal.setName(mealRequest.name());
    Set<MealIngredient> mealIngredients = new HashSet<>();
    for (var entry : mealRequest.ingredients().entrySet()) {
      if (entry.getValue() <= 0) {
        throw new IllegalArgumentException("Ingredient weight must be greater than zero");
      }
      MealIngredient ingredient = new MealIngredient();
      ingredient.setMeal(meal);
      Ingredient foundIngredient = ingredientRepository.findById(entry.getKey()).orElseThrow(
          () -> new EntityNotFoundException("Ingredient with id " + entry.getKey() + " not found"));
      ingredient.setIngredient(foundIngredient);
      ingredient.setWeight(entry.getValue());
      mealIngredients.add(ingredient);
    }
    meal.setMealIngredients(mealIngredients);
    Meal savedMeal = mealRepository.save(meal);
    return mapToMealResponse(savedMeal);
  }
}
