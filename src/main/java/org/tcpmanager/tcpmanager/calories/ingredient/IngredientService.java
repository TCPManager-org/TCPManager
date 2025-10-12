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

  private static String generateNotFoundMessage(Long id) {
    return "Ingredient with id " + id + " not found";
  }

  public List<IngredientResponse> getAll() {
    return ingredientRepository.findAll().stream().map(this::mapToIngredientResponse).toList();
  }

  public IngredientResponse getById(Long id) {
    Ingredient ingredient = ingredientRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    return mapToIngredientResponse(ingredient);
  }

  @Transactional
  public void deleteById(Long id) {
    if (!ingredientRepository.existsById(id)) {
      throw new EntityNotFoundException(generateNotFoundMessage(id));
    }

    ingredientRepository.deleteById(id);
  }

  @Transactional
  public IngredientResponse updateById(Long id, IngredientRequest ingredientRequest) {
    Ingredient ingredient = ingredientRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    if (ingredientRequest.name() != null && !ingredientRequest.name().isBlank()) {
      ingredient.setName(ingredientRequest.name());
    }
    if (ingredientRequest.calories() != null) {
      ingredient.setCalories(ingredientRequest.calories());
    }
    if (ingredientRequest.fats() != null) {
      ingredient.setFats(ingredientRequest.fats());
    }
    if (ingredientRequest.carbs() != null) {
      ingredient.setCarbs(ingredientRequest.carbs());
    }
    if (ingredientRequest.proteins() != null) {
      ingredient.setProteins(ingredientRequest.proteins());
    }
    if (ingredientRequest.ean() != null && !ingredientRequest.ean().isBlank()) {
      validateEan(ingredientRequest.ean());
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
    ingredient.setFats(ingredientRequest.fats());
    ingredient.setCarbs(ingredientRequest.carbs());
    ingredient.setProteins(ingredientRequest.proteins());
    ingredient.setEan(ingredientRequest.ean());
    ingredient = ingredientRepository.save(ingredient);
    return mapToIngredientResponse(ingredient);
  }

  private IngredientResponse mapToIngredientResponse(Ingredient ingredient) {
    return new IngredientResponse(ingredient.getId(), ingredient.getName(),
        ingredient.getCalories(), ingredient.getFats(), ingredient.getCarbs(),
        ingredient.getProteins(), ingredient.getEan());
  }

  private void validateIngredientRequest(IngredientRequest ingredientRequest) {
    if (ingredientRequest.name() == null || ingredientRequest.name().isBlank()) {
      throw new IllegalArgumentException("Name must not be blank");
    }
    if (ingredientRequest.calories() == null
        || ingredientRequest.calories().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Calories must be greater than 0");
    }
    if (ingredientRequest.fats() == null
        || ingredientRequest.fats().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Fats must be greater than 0");
    }
    if (ingredientRequest.carbs() == null
        || ingredientRequest.carbs().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Carbs must be greater than 0");
    }
    if (ingredientRequest.proteins() == null
        || ingredientRequest.proteins().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Proteins must be greater than 0");
    }
    validateEan(ingredientRequest.ean());
  }

  private void validateEan(String ean) {
    if (ean == null || ean.isBlank() || ean.length() != 13) {
      throw new IllegalArgumentException("EAN must be 13 characters long");
    }
    int sum = 0;
    for (int i = 0; i < 12; i++) {
      if (!Character.isDigit(ean.charAt(i))) {
        throw new IllegalArgumentException("EAN must contain only digits");
      }
      if (i % 2 == 0) {
        sum += Character.getNumericValue(ean.charAt(i));
      } else {
        sum += Character.getNumericValue(ean.charAt(i)) * 3;
      }
    }
    sum += Character.getNumericValue(ean.charAt(ean.length() - 1));
    if (sum % 10 != 0) {
      throw new IllegalArgumentException("EAN is not valid");
    }
  }

}
