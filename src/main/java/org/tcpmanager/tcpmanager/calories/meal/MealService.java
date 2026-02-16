package org.tcpmanager.tcpmanager.calories.meal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;
import org.tcpmanager.tcpmanager.calories.ingredient.IngredientRepository;
import org.tcpmanager.tcpmanager.calories.ingredient.IngredientService;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealPatch;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealRequest;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealResponse;
import org.tcpmanager.tcpmanager.calories.meal.models.Meal;
import org.tcpmanager.tcpmanager.calories.meal.models.MealIngredient;
import org.tcpmanager.tcpmanager.user.User;
import org.tcpmanager.tcpmanager.user.UserRepository;
import org.tcpmanager.tcpmanager.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealService {

  private final MealRepository mealRepository;
  private final IngredientRepository ingredientRepository;
  private final UserRepository userRepository;

  public static String generateNotFoundMessage(Long id) {
    return "Meal with id " + id + " not found";
  }

  private boolean isMealAvailableToUser(String username, Meal meal) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException(UserService.generateNotFoundMessage(username)));
    return user.equals(meal.getUser()) || meal.getUser() == null;
  }

  private boolean isIngredientAvailableToUser(String username, Ingredient ingredient) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException(UserService.generateNotFoundMessage(username)));
    return user.equals(ingredient.getUser()) || ingredient.getUser() == null;
  }

  public static MealResponse mapToMealResponse(Meal meal) {
    BigDecimal calories = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    BigDecimal carbs = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    BigDecimal fats = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    BigDecimal proteins = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    int weight = 0;
    Map<Long, String> ingredients = new HashMap<>();
    for (var mealIngredient : meal.getMealIngredients()) {
      Ingredient ingredient = mealIngredient.getIngredient();
      calories = calories.add(ingredient.getCalories().multiply(
          BigDecimal.valueOf(mealIngredient.getWeight())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN)));
      carbs = carbs.add(ingredient.getCarbs().multiply(
          BigDecimal.valueOf(mealIngredient.getWeight())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN)));
      fats = fats.add(ingredient.getFats().multiply(BigDecimal.valueOf(mealIngredient.getWeight())
          .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN)));
      proteins = proteins.add(ingredient.getProteins().multiply(
          BigDecimal.valueOf(mealIngredient.getWeight())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN)));
      ingredients.put(ingredient.getId(), ingredient.getName());
      weight += mealIngredient.getWeight();
    }
    return new MealResponse(meal.getId(), meal.getName(), weight, calories, carbs, fats, proteins,
        ingredients);
  }

  public List<MealResponse> getMeals(Integer minIngredients, Integer maxIngredients, String username) {
    var meals = mealRepository.findAll().stream()
        .filter(meal -> isMealAvailableToUser(username, meal))
        .toList();

    if (minIngredients != null) {
      meals = meals.stream()
          .filter(meal -> meal.getMealIngredients().size() >= minIngredients)
          .toList();
    }
    if (maxIngredients != null) {
      meals = meals.stream()
          .filter(meal -> meal.getMealIngredients().size() <= maxIngredients)
          .toList();
    }
    return meals.stream().map(MealService::mapToMealResponse).toList();
  }

  public MealResponse getMealById(Long id, String username) {
    Meal meal = mealRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    if (!isMealAvailableToUser(username, meal)) {
      throw new SecurityException("User is not allowed to modify this meal");
    }
    return mapToMealResponse(meal);
  }

  @Transactional
  public MealResponse addMeal(@Valid MealRequest mealRequest, String username) {
    if (mealRepository.existsByName(mealRequest.name())) {
      throw new IllegalArgumentException(
          "Meal with name " + mealRequest.name() + " already exists");
    }

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException(UserService.generateNotFoundMessage(username)));

    Meal meal = new Meal();
    meal.setName(mealRequest.name());
    meal.setUser(user);
    meal.setMealIngredients(mapToMealIngredients(meal, mealRequest.ingredients(), username));

    Meal savedMeal = mealRepository.save(meal);
    return mapToMealResponse(savedMeal);
  }


  @Transactional
  public MealResponse updateMealById(Long id, MealPatch mealPatch, String username) {
    Meal meal = mealRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));

    if (!isMealAvailableToUser(username, meal)) {
      throw new SecurityException("User is not allowed to modify this meal");
    }

    if (mealPatch.name() != null) {
      if (mealPatch.name().isBlank()) {
        throw new IllegalArgumentException("Meal name cannot be blank");
      }
      meal.setName(mealPatch.name());
    }
    if (mealPatch.ingredients() != null) {
      mergeMealIngredients(meal, mealPatch.ingredients(), username);
    }
    Meal updatedMeal = mealRepository.save(meal);
    return mapToMealResponse(updatedMeal);
  }

  @Transactional
  public void deleteById(Long id, String username) {
    Meal meal = mealRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));

    if (!isMealAvailableToUser(username, meal)) {
      throw new SecurityException("User is not allowed to modify this meal");
    }

    mealRepository.deleteById(id);
  }

  private void mergeMealIngredients(Meal meal, Map<Long, Integer> patch, String username) {
    Map<Long, MealIngredient> existing = new HashMap<>();
    for (MealIngredient mi : meal.getMealIngredients()) {
      existing.put(mi.getIngredient().getId(), mi);
    }

    for (Map.Entry<Long, Integer> e : patch.entrySet()) {
      Long ingredientId = e.getKey();
      Integer weight = e.getValue();

      if (weight == null || weight <= 0) {
        throw new IllegalArgumentException("Ingredient weight must be greater than zero");
      }

      MealIngredient mi = existing.get(ingredientId);
      if (mi != null) {
        mi.setWeight(weight);
        continue;
      }

      Ingredient ing = ingredientRepository.findById(ingredientId).orElseThrow(
          () -> new EntityNotFoundException(
              IngredientService.generateNotFoundMessage(ingredientId)));

      if (!isIngredientAvailableToUser(username, ing)) {
        throw new SecurityException("User is not allowed to use this ingredient");
      }

      MealIngredient created = new MealIngredient();
      created.setMeal(meal);
      created.setIngredient(ing);
      created.setWeight(weight);
      meal.getMealIngredients().add(created);
    }
  }

  private Set<MealIngredient> mapToMealIngredients(Meal meal, Map<Long, Integer> ingredients,
      String username) {
    Set<MealIngredient> mealIngredients = new HashSet<>();
    for (var entry : ingredients.entrySet()) {
      if (entry.getValue() == null || entry.getValue() <= 0) {
        throw new IllegalArgumentException("Ingredient weight must be greater than zero");
      }
      MealIngredient mealIngredient = new MealIngredient();
      Ingredient foundIngredient = ingredientRepository.findById(entry.getKey()).orElseThrow(
          () -> new EntityNotFoundException(
              IngredientService.generateNotFoundMessage(entry.getKey())));

      if (!isIngredientAvailableToUser(username, foundIngredient)) {
        throw new SecurityException("User is not allowed to use this ingredient");
      }

      mealIngredient.setIngredient(foundIngredient);
      mealIngredient.setWeight(entry.getValue());
      mealIngredient.setMeal(meal);
      mealIngredients.add(mealIngredient);
    }
    return mealIngredients;
  }
}
