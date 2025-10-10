package org.tcpmanager.tcpmanager.calories.mealingredient;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientResponse;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealResponse;
import org.tcpmanager.tcpmanager.calories.mealingredient.dto.MealIngredientResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MealIngredientService {

  private final MealIngredientRepository mealIngredientRepository;

  public List<MealIngredientResponse> getAll() {
    return mealIngredientRepository.findAll().stream().map(
        mi -> new MealIngredientResponse(mi.getId(),
            new MealResponse(mi.getMeal().getId(), mi.getMeal().getName()),
            mapToIngredientResponse(mi.getIngredient()), mi.getAmount())).toList();
  }

  private IngredientResponse mapToIngredientResponse(Ingredient ingredient) {
    return new IngredientResponse(ingredient.getId(), ingredient.getName(),
        ingredient.getCalories(), ingredient.getFats(), ingredient.getCarbs(),
        ingredient.getProteins(), ingredient.getEan());
  }
}
