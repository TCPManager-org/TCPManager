package org.tcpmanager.tcpmanager.calories.ingredient;

import jakarta.validation.Valid;

import java.security.Principal;
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

  @GetMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public List<IngredientResponse> getIngredients(Principal principal) {
    return ingredientService.getAllIngredientsByUser(principal.getName());
  }

  @GetMapping(value = "/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public IngredientResponse getIngredientById(@PathVariable Long id, Principal principal) {
    return ingredientService.getIngredientById(id, principal.getName());
  }

  @PostMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public IngredientResponse addIngredient(@RequestBody @Valid IngredientRequest ingredientRequest, Principal principal) {
    return ingredientService.addIngredient(ingredientRequest, principal.getName());
  }

  @PatchMapping(value = "/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public IngredientResponse updateIngredientById(@PathVariable Long id,
      @RequestBody @Valid IngredientPatch ingredientPatch, Principal principal) {
    return ingredientService.updateIngredientById(id, ingredientPatch, principal.getName());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteIngredientById(@PathVariable Long id, Principal principal) {
    ingredientService.deleteIngredientById(id, principal.getName());
  }
}
