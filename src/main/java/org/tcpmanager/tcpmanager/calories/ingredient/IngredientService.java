package org.tcpmanager.tcpmanager.calories.ingredient;

import jakarta.persistence.EntityNotFoundException;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientPatch;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientRequest;
import org.tcpmanager.tcpmanager.calories.ingredient.dto.IngredientResponse;
import org.tcpmanager.tcpmanager.calories.meal.models.Meal;
import org.tcpmanager.tcpmanager.calories.meal.models.MealIngredient;
import org.tcpmanager.tcpmanager.user.User;
import org.tcpmanager.tcpmanager.user.UserRepository;
import org.tcpmanager.tcpmanager.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

  private final IngredientRepository ingredientRepository;
  private final UserRepository userRepository;

  public static String generateNotFoundMessage(Long id) {
    return "Ingredient with id " + id + " not found";
  }

  private static void throwSecurityException() {
    throw new SecurityException("User is not allowed to modify this ingredient");
  }

  private static IngredientResponse mapToIngredientResponse(Ingredient ingredient) {
    return new IngredientResponse(ingredient.getId(), ingredient.getName(),
        ingredient.getCalories(), ingredient.getFats(), ingredient.getCarbs(),
        ingredient.getProteins(), ingredient.getEan());
  }

  public List<IngredientResponse> getAllIngredientsByUser(String username) {
    return ingredientRepository.findAll().stream()
        .filter(i -> isIngredientAvailableToUser(username, i))
        .map(IngredientService::mapToIngredientResponse)
        .toList();
  }

  public IngredientResponse getIngredientById(Long id, String username) {
    Ingredient ingredient = ingredientRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    if (!isIngredientAvailableToUser(username, ingredient)) {
      throwSecurityException();
    }
    return mapToIngredientResponse(ingredient);
  }

  @Transactional
  public IngredientResponse addIngredient(IngredientRequest ingredientRequest, String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(
            () -> new EntityNotFoundException(UserService.generateNotFoundMessage(username)));
    if (ingredientRequest.ean() != null) {
      validateEan(ingredientRequest.ean().strip());
    }
    Ingredient ingredient = new Ingredient();
    ingredient.setName(ingredientRequest.name().strip());
    ingredient.setCalories(ingredientRequest.calories());
    ingredient.setFats(ingredientRequest.fats());
    ingredient.setCarbs(ingredientRequest.carbs());
    ingredient.setProteins(ingredientRequest.proteins());
    ingredient.setUser(user);
    if (ingredientRequest.ean() != null) {
      ingredient.setEan(ingredientRequest.ean().strip());
    }
    ingredient = ingredientRepository.save(ingredient);
    return mapToIngredientResponse(ingredient);
  }

  @Transactional
  public IngredientResponse updateIngredientById(Long id, IngredientPatch ingredientPatch,
      String username) {
    Ingredient ingredient = ingredientRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    if (!isIngredientAvailableToUser(username, ingredient)) {
      throwSecurityException();
    }
    if (ingredientPatch.name() != null) {
      if (ingredientPatch.name().isBlank()) {
        throw new IllegalArgumentException("Name must not be blank");
      }
      ingredient.setName(ingredientPatch.name().strip());
    }
    if (ingredientPatch.calories() != null) {
      ingredient.setCalories(ingredientPatch.calories());
    }
    if (ingredientPatch.fats() != null) {
      ingredient.setFats(ingredientPatch.fats().setScale(2, RoundingMode.HALF_EVEN));
    }
    if (ingredientPatch.carbs() != null) {
      ingredient.setCarbs(ingredientPatch.carbs().setScale(2, RoundingMode.HALF_EVEN));
    }
    if (ingredientPatch.proteins() != null) {
      ingredient.setProteins(ingredientPatch.proteins().setScale(2, RoundingMode.HALF_EVEN));
    }
    if (ingredientPatch.ean() != null) {
      validateEan(ingredientPatch.ean().strip());
      ingredient.setEan(ingredientPatch.ean().strip());
    }
    ingredientRepository.save(ingredient);
    return mapToIngredientResponse(ingredient);
  }

  @Transactional
  public void deleteIngredientById(Long id, String username) {
    Ingredient ingredient = ingredientRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    if (!isIngredientAvailableToUser(username, ingredient)) {
      throwSecurityException();
    }
    Set<Meal> meals = ingredient.getMealIngredients().stream().map(MealIngredient::getMeal)
        .collect(Collectors.toSet());
    if (!meals.isEmpty()) {
      throw new IllegalArgumentException("Ingredient is used in meals and cannot be deleted");
    }
    ingredientRepository.deleteById(id);
  }

  private void validateEan(String ean) {
    if (ean.isBlank()) {
      throw new IllegalArgumentException("Ean must not be blank");
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
    var existing = ingredientRepository.findByEan(ean);
    if (existing.isPresent()) {
      throw new IllegalArgumentException("EAN must be unique");
    }
  }

  private boolean isIngredientAvailableToUser(String username, Ingredient ingredient) {
    User user = userRepository.findByUsername(username).orElseThrow(
        () -> new EntityNotFoundException(UserService.generateNotFoundMessage(username)));
    return user.equals(ingredient.getUser());
  }
}
