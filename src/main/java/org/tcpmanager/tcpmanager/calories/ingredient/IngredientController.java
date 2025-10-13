package org.tcpmanager.tcpmanager.calories.ingredient;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientPatch;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientRequest;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientResponse;

@RestController
@RequestMapping("/api/calories/ingredients")
@RequiredArgsConstructor
public class IngredientController {

  private final IngredientService ingredientService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<IngredientResponse> getMeals() {
    return ingredientService.getAll();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public IngredientResponse getMealById(@PathVariable Long id) {
    return ingredientService.getById(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteMealById(@PathVariable Long id) {
    ingredientService.deleteById(id);
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public IngredientResponse updateMealById(@PathVariable Long id,
      @RequestBody @Valid IngredientPatch ingredientPatch) {
    return ingredientService.updateById(id, ingredientPatch);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public IngredientResponse addMeal(@RequestBody @Valid IngredientRequest ingredientRequest) {
    return ingredientService.add(ingredientRequest);
  }
}
