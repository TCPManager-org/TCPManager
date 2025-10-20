package org.tcpmanager.tcpmanager.calories.mealingredient;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.tcpmanager.tcpmanager.calories.mealingredient.dto.MealIngredientResponse;

@RestController
@RequestMapping("/api/calories/meal-ingredients")
@RequiredArgsConstructor
public class MealIngredientController {

  private final MealIngredientService mealIngredientService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<MealIngredientResponse> getAll() {
    return mealIngredientService.getAll();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public MealIngredientResponse getMealIngredientById(@PathVariable Long id) {
    return mealIngredientService.getById(id);
  }
  @GetMapping(params = "ingredientName")
  @ResponseStatus(HttpStatus.OK)
  public List<MealIngredientResponse> getALlMealIngredientsByIngredientName(@RequestParam(value = "ingredientName") String ingredientName) {
    return mealIngredientService.getAllMealIngredientsByIngredientName(ingredientName);
  }
}
