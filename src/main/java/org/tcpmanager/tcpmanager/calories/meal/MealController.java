package org.tcpmanager.tcpmanager.calories.meal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealPatch;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealRequest;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealResponse;

@RestController
@RequestMapping("/api/calories/meals")
@RequiredArgsConstructor
public class MealController {

  private final MealService mealService;

  @GetMapping(produces = "application/json")
  public List<MealResponse> getMeals(
      @RequestParam(required = false) @Min(1) Integer minIngredients,
      @RequestParam(required = false) @Min(1) Integer maxIngredients) {
    return mealService.getMeals(minIngredients, maxIngredients);
  }

  @GetMapping(value = "/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public MealResponse getMealById(@PathVariable Long id) {
    return mealService.getById(id);
  }


  @PostMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public MealResponse addMeal(@RequestBody @Valid MealRequest mealRequest) {
    return mealService.addMeal(mealRequest);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteMealById(@PathVariable Long id) {
    mealService.deleteById(id);
  }

  @PatchMapping(value = "/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public MealResponse updateMealById(@PathVariable Long id,
      @RequestBody @Valid MealPatch mealPatch) {
    return mealService.updateById(id, mealPatch);
  }
}
