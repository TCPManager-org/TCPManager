package org.tcpmanager.tcpmanager.calories;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;
import org.tcpmanager.tcpmanager.calories.ingredient.IngredientRepository;
import org.tcpmanager.tcpmanager.calories.meal.MealRepository;
import org.tcpmanager.tcpmanager.calories.meal.models.Meal;
import org.tcpmanager.tcpmanager.calories.meal.models.MealIngredient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class MealTests {

  @SuppressWarnings("resource")
  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
      "postgres:17").withDatabaseName("tcp").withUsername("root").withPassword("root");

  static {
    postgreSQLContainer.start();
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MealRepository mealRepository;
  @Autowired
  private IngredientRepository ingredientRepository;

  private static Meal createMeal() {
    MealIngredient mealIngredient1 = new MealIngredient();
    Meal meal = new Meal();
    meal.setName("Test Meal");
    meal.setFavorite(false);

    mealIngredient1.setMeal(meal);
    Ingredient ingredient1 = new Ingredient();
    ingredient1.setName("Test Ingredient1");
    ingredient1.setCalories(BigDecimal.valueOf(100));
    ingredient1.setFats(BigDecimal.valueOf(10));
    ingredient1.setCarbs(BigDecimal.valueOf(20));
    ingredient1.setProteins(BigDecimal.valueOf(30));
    ingredient1.setEan("0123456789012");

    mealIngredient1.setIngredient(ingredient1);
    mealIngredient1.setWeight(100);

    MealIngredient mealIngredient2 = new MealIngredient();
    mealIngredient2.setMeal(meal);
    Ingredient ingredient2 = new Ingredient();
    ingredient2.setName("Test Ingredient2");
    ingredient2.setCalories(BigDecimal.valueOf(1000));
    ingredient2.setFats(BigDecimal.valueOf(100));
    ingredient2.setCarbs(BigDecimal.valueOf(200));
    ingredient2.setProteins(BigDecimal.valueOf(300));
    ingredient2.setEan("2345678901234");
    mealIngredient2.setIngredient(ingredient2);
    mealIngredient2.setWeight(100);
    meal.setMealIngredients(Set.of(mealIngredient1, mealIngredient2));
    return meal;
  }

  @AfterEach
  void afterEach() {
    mealRepository.deleteAll();
    ingredientRepository.deleteAll();
  }

  @Test
  void getAllMeals_ShouldReturnMeals() throws Exception {
    Meal meal = createMeal();
    meal = mealRepository.save(meal);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/meals")).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1)).andExpect(jsonPath("$[0].id").value(meal.getId()))
        .andExpect(jsonPath("$[0].calories").value(BigDecimal.valueOf(1100.0)))
        .andExpect(jsonPath("$[0].ingredients.size()").value(2));
  }

  @Test
  void getMealById_ShouldReturnMeal() throws Exception {
    Meal meal = createMeal();
    meal = mealRepository.save(meal);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/meals/" + meal.getId()))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(meal.getId()))
        .andExpect(jsonPath("$.id").value(meal.getId()))
        .andExpect(jsonPath("$.ingredients.size()").value(2));
  }

  @Test
  void getMealById_ShouldReturnNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/meals/9999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Meal with id 9999 not found"));
  }

  @Test
  void addMeal_ShouldCreateMeal() throws Exception {
    Ingredient ingredient1 = new Ingredient();
    ingredient1.setName("Test Ingredient1");
    ingredient1.setCalories(BigDecimal.valueOf(100));
    ingredient1.setFats(BigDecimal.valueOf(10));
    ingredient1.setCarbs(BigDecimal.valueOf(20));
    ingredient1.setProteins(BigDecimal.valueOf(30));
    ingredient1.setEan("0123456789012");
    ingredient1 = ingredientRepository.save(ingredient1);

    Ingredient ingredient2 = new Ingredient();
    ingredient2.setName("Test Ingredient2");
    ingredient2.setCalories(BigDecimal.valueOf(1000));
    ingredient2.setFats(BigDecimal.valueOf(100));
    ingredient2.setCarbs(BigDecimal.valueOf(200));
    ingredient2.setProteins(BigDecimal.valueOf(300));
    ingredient2.setEan("2345678901234");
    ingredient2 = ingredientRepository.save(ingredient2);

    String mealJson = """
        {
          "name": "New Meal",
          "favorite": false,
          "ingredients": {
            "%d": 150,
            "%d": 200
          }
        }
        """.formatted(ingredient1.getId(), ingredient2.getId());

    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/meals").contentType("application/json")
                .content(mealJson)).andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("New Meal"))
        .andExpect(jsonPath("$.calories").value(2150.0))
        .andExpect(jsonPath("$.ingredients.size()").value(2));
  }

  @Test
  void addMeal_ShouldReturnBadRequest_ForInvalidIngredient() throws Exception {
    String mealJson = """
        {
          "name": "New Meal",
          "favorite": false,
          "ingredients": {
            "9999": 150
          }
        }
        """;

    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/meals").contentType("application/json")
                .content(mealJson)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Ingredient with id 9999 not found"));
  }

  @Test
  void addMeal_ShouldReturnBadRequest_ForWeightZero() throws Exception {
    Ingredient ingredient1 = new Ingredient();
    ingredient1.setName("Test Ingredient1");
    ingredient1.setCalories(BigDecimal.valueOf(100));
    ingredient1.setFats(BigDecimal.valueOf(10));
    ingredient1.setCarbs(BigDecimal.valueOf(20));
    ingredient1.setProteins(BigDecimal.valueOf(30));
    ingredient1.setEan("0123456789012");
    ingredient1 = ingredientRepository.save(ingredient1);

    String mealJson = """
        {
          "name": "New Meal",
          "favorite": false,
          "ingredients": {
            "%d": 0
          }
        }
        """.formatted(ingredient1.getId());

    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/meals").contentType("application/json")
                .content(mealJson)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Ingredient weight must be greater than zero"));
  }

  @Test
  void deleteMeal_ShouldDeleteMeal() throws Exception {
    Meal meal = createMeal();
    meal = mealRepository.save(meal);
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/calories/meals/" + meal.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteMeal_ShouldReturnNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/calories/meals/9999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Meal with id 9999 not found"));
  }

  @Test
  void updateMeal_ShouldUpdateIngredients() throws Exception {
    Meal meal = createMeal();
    meal = mealRepository.save(meal);

    Ingredient ingredient3 = new Ingredient();
    ingredient3.setName("Test Ingredient3");
    ingredient3.setCalories(BigDecimal.valueOf(500));
    ingredient3.setFats(BigDecimal.valueOf(50));
    ingredient3.setCarbs(BigDecimal.valueOf(60));
    ingredient3.setProteins(BigDecimal.valueOf(70));
    ingredient3.setEan("3456789012345");
    ingredient3 = ingredientRepository.save(ingredient3);

    String mealPatchJson = """
        {
          "ingredients": {
            "%d": 200,
            "%d": 100
          }
        }
        """.formatted(ingredient3.getId(),
        meal.getMealIngredients().iterator().next().getIngredient().getId());

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/calories/meals/" + meal.getId())
            .contentType("application/json").content(mealPatchJson)).andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test Meal"))
        .andExpect(jsonPath("$.ingredients.size()").value(2));
  }

  @Test
  void updateMeal_ShouldUpdateName() throws Exception {
    Meal meal = createMeal();
    meal = mealRepository.save(meal);

    String mealPatchJson = """
        {
          "name": "Updated Meal"
        }
        """;

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/calories/meals/" + meal.getId())
            .contentType("application/json").content(mealPatchJson)).andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Meal"))
        .andExpect(jsonPath("$.ingredients.size()").value(2));
  }

  @Test
  void updateMeal_ShouldUpdateFavourite() throws Exception {
    Meal meal = createMeal();
    meal = mealRepository.save(meal);

    String mealPatchJson = """
        {
          "favorite": true
        }
        """;

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/calories/meals/" + meal.getId())
            .contentType("application/json").content(mealPatchJson)).andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test Meal"))
        .andExpect(jsonPath("$.ingredients.size()").value(2))
        .andExpect(jsonPath("$.favorite").value(true));
  }

  @Test
  void updateMeal_ShouldReturnNotFound() throws Exception {
    String mealPatchJson = """
        {
          "name": "Updated Meal"
        }
        """;

    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/calories/meals/9999").contentType("application/json")
                .content(mealPatchJson)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Meal with id 9999 not found"));
  }

  @Test
  void updateMeal_ShouldReturnBadRequest_ForBlankName() throws Exception {
    Meal meal = createMeal();
    meal = mealRepository.save(meal);

    String mealPatchJson = """
        {
          "name": "   "
        }
        """;

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/calories/meals/" + meal.getId())
            .contentType("application/json").content(mealPatchJson)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Meal name cannot be blank"));
  }

  @Test
  void getMealsByMinIngredients_ShouldReturnMeals() throws Exception {
    Meal meal1 = createMeal();
    meal1 = mealRepository.save(meal1);

    MealIngredient mealIngredient1 = new MealIngredient();
    Meal meal2 = new Meal();
    meal2.setName("Test Meal2");
    meal2.setFavorite(false);

    mealIngredient1.setMeal(meal2);
    Ingredient ingredient1 = new Ingredient();
    ingredient1.setName("Test Ingredient11");
    ingredient1.setCalories(BigDecimal.valueOf(100));
    ingredient1.setFats(BigDecimal.valueOf(10));
    ingredient1.setCarbs(BigDecimal.valueOf(20));
    ingredient1.setProteins(BigDecimal.valueOf(30));
    ingredient1.setEan("978020137962");

    mealIngredient1.setIngredient(ingredient1);
    mealIngredient1.setWeight(100);

    meal2.setMealIngredients(Set.of(mealIngredient1));
    mealRepository.save(meal2);

    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/calories/meals?minIngredients=2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].id").value(meal1.getId()))
        .andExpect(jsonPath("$[0].calories").value(BigDecimal.valueOf(1100.0)))
        .andExpect(jsonPath("$[0].ingredients.size()").value(2));
  }

}