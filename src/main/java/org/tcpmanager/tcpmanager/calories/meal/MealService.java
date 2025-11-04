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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;
import org.tcpmanager.tcpmanager.calories.ingredient.IngredientRepository;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealPatch;
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

  private static MealResponse mapToMealResponse(Meal meal) {
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
      fats = fats.add(ingredient.getFats().multiply(BigDecimal.valueOf(mealIngredient.getWeight())
          .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN)));
      proteins = proteins.add(ingredient.getProteins().multiply(
          BigDecimal.valueOf(mealIngredient.getWeight())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN)));
      ingredients.put(ingredient.getId(), ingredient.getName());
    }
    return new MealResponse(meal.getId(), meal.getName(), meal.getFavorite(), calories, carbs, fats, proteins,
        ingredients);
  }

  public List<MealResponse> getAllMeals() {
    return mealRepository.findAll().stream().map(MealService::mapToMealResponse).toList();
  }

  public MealResponse getById(Long id) {
    Meal meal = mealRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    return mapToMealResponse(meal);
  }

  @Transactional
  public MealResponse addMeal(@Valid MealRequest mealRequest) {
    Meal meal = new Meal();
    meal.setName(mealRequest.name());
    meal.setFavorite(mealRequest.favorite());
    meal.setMealIngredients(mapToMealIngredients(meal, mealRequest.ingredients()));
    Meal savedMeal = mealRepository.save(meal);
    return mapToMealResponse(savedMeal);
  }

  @Transactional
  public void deleteById(Long id) {
    if (!mealRepository.existsById(id)) {
      throw new EntityNotFoundException(generateNotFoundMessage(id));
    }
    mealRepository.deleteById(id);
  }

  public MealResponse updateById(Long id, MealPatch mealPatch) {
    Meal meal = mealRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    if (mealPatch.name() != null) {
      if (mealPatch.name().isBlank()) {
        throw new IllegalArgumentException("Meal name cannot be blank");
      }
      meal.setName(mealPatch.name());
    }
    if (mealPatch.ingredients() != null) {
      meal.setMealIngredients(mapToMealIngredients(meal, mealPatch.ingredients()));
    }
    if(mealPatch.favorite() != null) {
      meal.setFavorite(mealPatch.favorite());
    }
    Meal updatedMeal = mealRepository.save(meal);
    return mapToMealResponse(updatedMeal);
  }

  private Set<MealIngredient> mapToMealIngredients(Meal meal, Map<Long, Integer> ingredients) {
    Set<MealIngredient> mealIngredients = new HashSet<>();
    for (var entry : ingredients.entrySet()) {
      if (entry.getValue() <= 0) {
        throw new IllegalArgumentException("Ingredient weight must be greater than zero");
      }
      MealIngredient mealIngredient = new MealIngredient();
      Ingredient foundIngredient = ingredientRepository.findById(entry.getKey()).orElseThrow(
          () -> new EntityNotFoundException("Ingredient with id " + entry.getKey() + " not found"));
      mealIngredient.setIngredient(foundIngredient);
      mealIngredient.setWeight(entry.getValue());
      mealIngredient.setMeal(meal);
      mealIngredients.add(mealIngredient);
    }
    return mealIngredients;
  }
}
