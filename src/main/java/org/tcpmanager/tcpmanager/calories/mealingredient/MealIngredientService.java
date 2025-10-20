package org.tcpmanager.tcpmanager.calories.mealingredient;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientResponse;
import org.tcpmanager.tcpmanager.calories.meal.Meal;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealResponse;
import org.tcpmanager.tcpmanager.calories.mealingredient.dto.MealIngredientResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MealIngredientService {

  private final MealIngredientRepository mealIngredientRepository;

  private static String generateNotFoundMessage(Long id) {
    return "Meal Ingredient with id " + id + " not found";
  }

  public List<MealIngredientResponse> getAll() {
    return mealIngredientRepository.findAll().stream().map(this::mapToMealIngredientResponse).toList();
  }

  public MealIngredientResponse getById(Long id) {
    return mealIngredientRepository.findById(id).map(this::mapToMealIngredientResponse)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
  }
  private MealIngredientResponse mapToMealIngredientResponse(MealIngredient mealIngredient) {
    return new MealIngredientResponse(
        mealIngredient.getId(),
        mapToMealResponse(mealIngredient.getMeal()),
        mapToIngredientResponse(mealIngredient.getIngredient()),
        mealIngredient.getAmount()
    );
  }
  private MealResponse mapToMealResponse(Meal meal) {
    return new MealResponse(
        meal.getId(),
        meal.getName()
    );
  }
  private IngredientResponse mapToIngredientResponse(Ingredient ingredient) {
    return new IngredientResponse(ingredient.getId(), ingredient.getName(),
        ingredient.getCalories(), ingredient.getFats(), ingredient.getCarbs(),
        ingredient.getProteins(), ingredient.getEan());
  }

  public List<MealIngredientResponse> getAllMealIngredientsByIngredientName(String ingredientName) {
    return mealIngredientRepository.findAllByIngredientName(ingredientName).stream()
        .map(this::mapToMealIngredientResponse).toList();
  }
}
