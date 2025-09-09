package org.tcpmanager.tcpmanager.calories.meal_ingredient;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.dto.IngredientResponse;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealResponse;
import org.tcpmanager.tcpmanager.calories.meal_ingredient.dto.MealIngredientResponse;
import org.tcpmanager.tcpmanager.calories.models.Ingredient;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MealIngredientService {
  private final MealIngredientRepository mealIngredientRepository;
  public List<MealIngredientResponse> getAll() {
    return mealIngredientRepository.findAll().stream().map(mi ->
        new MealIngredientResponse(mi.getId(),
            new MealResponse(mi.getMeal().getId(), mi.getMeal().getName()),
            mapToIngredientResponse(mi.getIngredient()),
            mi.getAmount()
            )).toList();
  }
  private IngredientResponse mapToIngredientResponse(Ingredient ingredient) {
    return new IngredientResponse(ingredient.getId(), ingredient.getName(), ingredient.getCalories(), ingredient.getFat(), ingredient.getCarbs(), ingredient.getProtein(), ingredient.getEan());
  }
}
