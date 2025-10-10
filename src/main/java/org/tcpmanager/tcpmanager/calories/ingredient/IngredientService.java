package org.tcpmanager.tcpmanager.calories.ingredient;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientRequest;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

  private final IngredientRepository ingredientRepository;

  public List<IngredientResponse> getAll() {
    return ingredientRepository.findAll().stream().map(this::mapToIngredientResponse).toList();
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
    if (ingredientRequest.name() != null && !ingredientRequest.name().isBlank()) {
      ingredient.setName(ingredientRequest.name());
    }
    if (ingredientRequest.calories() != null) {
      ingredient.setCalories(ingredientRequest.calories());
    }
    if (ingredientRequest.fat() != null) {
      ingredient.setFat(ingredientRequest.fat());
    }
    if (ingredientRequest.carbs() != null) {
      ingredient.setCarbs(ingredientRequest.carbs());
    }
    if (ingredientRequest.protein() != null) {
      ingredient.setProtein(ingredientRequest.protein());
    }
    if (ingredientRequest.ean() != null && !ingredientRequest.ean().isBlank()) {
      ingredient.setEan(ingredientRequest.ean());
    }
    ingredientRepository.save(ingredient);
    return mapToIngredientResponse(ingredient);
  }

  @Transactional
  public IngredientResponse add(IngredientRequest ingredientRequest) {
    validateIngredientRequest(ingredientRequest);
    Ingredient ingredient = new Ingredient();
    ingredient.setName(ingredientRequest.name());
    ingredient.setCalories(ingredientRequest.calories());
    ingredient.setFat(ingredientRequest.fat());
    ingredient.setCarbs(ingredientRequest.carbs());
    ingredient.setProtein(ingredientRequest.protein());
    ingredient.setEan(ingredientRequest.ean());
    ingredient = ingredientRepository.save(ingredient);
    return mapToIngredientResponse(ingredient);
  }

  private IngredientResponse mapToIngredientResponse(Ingredient ingredient) {
    return new IngredientResponse(ingredient.getId(), ingredient.getName(),
        ingredient.getCalories(), ingredient.getFat(), ingredient.getCarbs(),
        ingredient.getProtein(), ingredient.getEan());
  }

  private void validateIngredientRequest(IngredientRequest ingredientRequest) {
    if (ingredientRequest.name() == null || ingredientRequest.name().isBlank()) {
      throw new IllegalArgumentException("Name must not be blank");
    }
    if (ingredientRequest.calories() == null
        || ingredientRequest.calories().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Calories must be greater than 0");
    }
    if (ingredientRequest.fat() == null
        || ingredientRequest.fat().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Fat must be greater than 0");
    }
    if (ingredientRequest.carbs() == null
        || ingredientRequest.carbs().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Carbs must be greater than 0");
    }
    if (ingredientRequest.protein() == null
        || ingredientRequest.protein().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Protein must be greater than 0");
    }
  }
}
