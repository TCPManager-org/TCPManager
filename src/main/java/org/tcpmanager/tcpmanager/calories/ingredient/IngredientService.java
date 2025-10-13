package org.tcpmanager.tcpmanager.calories.ingredient;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientPatch;
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
  public IngredientResponse updateById(Long id, IngredientPatch ingredientPatch) {
    Ingredient ingredient = ingredientRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    if (ingredientPatch.name() != null) {
      if (ingredientPatch.name().isBlank()) {
        throw new IllegalArgumentException("Name must not be blank");
      }
      ingredient.setName(ingredientPatch.name());
    }
    if (ingredientPatch.calories() != null) {
      ingredient.setCalories(ingredientPatch.calories());
    }
    if (ingredientPatch.fats() != null) {
      ingredient.setFats(ingredientPatch.fats());
    }
    if (ingredientPatch.carbs() != null) {
      ingredient.setCarbs(ingredientPatch.carbs());
    }
    if (ingredientPatch.proteins() != null) {
      ingredient.setProteins(ingredientPatch.proteins());
    }
    if (ingredientPatch.ean() != null && !ingredientPatch.ean().isBlank()) {
      validateEan(ingredientPatch.ean());
      ingredient.setEan(ingredientPatch.ean());
    }
    ingredientRepository.save(ingredient);
    return mapToIngredientResponse(ingredient);
  }

  @Transactional
  public IngredientResponse add(IngredientRequest ingredientRequest) {
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


  private void validateEan(String ean) {
    if (ean.isBlank() || ean.length() != 13) {
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
