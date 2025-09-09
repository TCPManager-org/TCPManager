package org.tcpmanager.tcpmanager.calories.meal_ingredient;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tcpmanager.tcpmanager.calories.meal_ingredient.dto.MealIngredientResponse;

@RestController
@RequestMapping("/api/meal-ingredients")
@RequiredArgsConstructor
public class MealIngredientController {
  private final MealIngredientService mealIngredientService;

  @GetMapping
  public List<MealIngredientResponse> getAll() {
    return mealIngredientService.getAll();
  }
}
