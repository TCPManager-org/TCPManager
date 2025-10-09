package org.tcpmanager.tcpmanager.calories.ingredient;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientRequest;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientResponse;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

  private final IngredientRepository ingredientRepository;

  public List<IngredientResponse> getAll() {
    return ingredientRepository.findAll().stream()
        .map(this::mapToIngredientResponse).toList();
  }

  public IngredientResponse getById(Long id) {
    Ingredient ingredient = ingredientRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Ingredient with id " + id + " not found"));
    return mapToIngredientResponse(ingredient);
  }

  @Transactional
  public void deleteById(Long id) {
    if (!ingredientRepository.existsById(id)) {
      throw new EntityNotFoundException("Ingredient with id " + id + " not found");
    }

    ingredientRepository.deleteById(id);
  }

  @Transactional
  public IngredientResponse updateById(Long id, IngredientRequest ingredientRequest) {
    Ingredient ingredient = ingredientRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Ingredient with id " + id + " not found"));
    ingredient.setName(ingredientRequest.name());
    ingredientRepository.save(ingredient);
    return mapToIngredientResponse(ingredient);
  }

  @Transactional
  public IngredientResponse add(IngredientRequest ingredientRequest) {
    Ingredient ingredient = new Ingredient();
    ingredient.setName(ingredientRequest.name());
    ingredient = ingredientRepository.save(ingredient);
    return mapToIngredientResponse(ingredient);
  }

  private IngredientResponse mapToIngredientResponse(Ingredient ingredient) {
    return new IngredientResponse(ingredient.getId(), ingredient.getName(),
        ingredient.getCalories(), ingredient.getFat(), ingredient.getCarbs(),
        ingredient.getProtein(), ingredient.getEan());
  }
}
