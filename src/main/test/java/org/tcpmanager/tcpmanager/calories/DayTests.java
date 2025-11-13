// java
package org.tcpmanager.tcpmanager.calories;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.tcpmanager.tcpmanager.calories.day.DayRepository;
import org.tcpmanager.tcpmanager.calories.day.models.Day;
import org.tcpmanager.tcpmanager.calories.day.models.DayMeal;
import org.tcpmanager.tcpmanager.calories.day.models.MealType;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;
import org.tcpmanager.tcpmanager.calories.ingredient.IngredientRepository;
import org.tcpmanager.tcpmanager.calories.meal.MealRepository;
import org.tcpmanager.tcpmanager.calories.meal.models.Meal;
import org.tcpmanager.tcpmanager.calories.meal.models.MealIngredient;
import org.tcpmanager.tcpmanager.user.User;
import org.tcpmanager.tcpmanager.user.UserRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class DayTests {

  @SuppressWarnings("resource")
  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:17").withDatabaseName("tcp").withUsername("root").withPassword("root");

  static {
    postgres.start();
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MealRepository mealRepository;
  @Autowired
  private IngredientRepository ingredientRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private DayRepository dayRepository;

  private static Meal createMeal() {
    MealIngredient mealIngredient1 = new MealIngredient();
    Meal meal = new Meal();
    meal.setName("Test Meal");

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

  private static @NotNull Meal createMeal2() {
    MealIngredient mealIngredient1 = new MealIngredient();
    Meal meal2 = new Meal();
    meal2.setName("Test Meal2");

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
    return meal2;
  }

  @AfterEach
  void cleanup() {
    dayRepository.deleteAll();
    mealRepository.deleteAll();
    ingredientRepository.deleteAll();
    userRepository.deleteAll();
  }

  private User createUser(String username) {
    User user = new User();
    user.setUsername(username);
    return userRepository.save(user);
  }

  @Test
  void addMealToDay_ShouldCreateDay() throws Exception {
    createUser("john");
    Meal meal = createMeal();
    meal = mealRepository.save(meal);
    String json = String.format("""
        {
          "date": "2024-01-01",
          "username": "john",
          "mealId": %d,
          "weight": 150,
          "mealType": "BREAKFAST"
        }
        """, meal.getId());

    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(json)).andExpect(status().isCreated())
        .andExpect(jsonPath("$.date").value("2024-01-01"))
        .andExpect(jsonPath("$.dayMeals.size()").value(1))
        .andExpect(jsonPath("$.dayMeals[0].weight").value(150))
        .andExpect(jsonPath("$.dayMeals[0].mealType").value("BREAKFAST"))
        .andExpect(jsonPath("$.dayMeals[0].meal.name").value("Test Meal"));
  }

  @Test
  void addMealToExistingDay_ShouldAppendMeal() throws Exception {
    createUser("john");
    Meal meal = createMeal();
    mealRepository.save(meal);
    Meal meal2 = createMeal2();
    mealRepository.save(meal2);

    String first = String.format("""
        {
          "date": "2024-02-01",
          "username": "john",
          "mealId": %d,
          "weight": 100,
          "mealType": "LUNCH"
        }
        """, meal.getId());
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
            .content(first)).andExpect(status().isCreated());

    String second = String.format("""
        {
          "date": "2024-02-01",
          "username": "john",
          "mealId": %d,
          "weight": 200,
          "mealType": "DINNER"
        }
        """, meal2.getId());
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(second)).andExpect(status().isCreated())
        .andExpect(jsonPath("$.dayMeals.size()").value(2));
  }

  @Test
  void addMealToDay_ShouldReturnNotFound_ForMissingUser() throws Exception {
    Meal meal = createMeal();
    mealRepository.save(meal);
    String json = String.format("""
        {
          "date": "2024-03-01",
          "username": "missing",
          "mealId": %d,
          "weight": 100,
          "mealType": "LUNCH"
        }
        """, meal.getId());
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(json)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with username missing not found"));
  }

  @Test
  void addMealToDay_ShouldReturnNotFound_ForMissingMeal() throws Exception {
    createUser("john");
    long missingId = 999999;
    String json = String.format("""
        {
          "date": "2024-03-02",
          "username": "john",
          "mealId": %d,
          "weight": 100,
          "mealType": "SNACK"
        }
        """, missingId);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(json)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Meal with id 999999 not found"));
  }

  @Test
  void getAllDays_ShouldReturnDays() throws Exception {
    User user = createUser("john");
    Meal meal = createMeal();
    mealRepository.save(meal);

    Day day = new Day();
    day.setDate(Date.valueOf("2024-04-01"));
    day.setUser(user);
    DayMeal dm = new DayMeal();
    dm.setDay(day);
    dm.setMeal(meal);
    dm.setMealType(MealType.BREAKFAST);
    dm.setWeight(120);
    day.setDayMeals(Set.of(dm));
    dayRepository.save(day);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/days")).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].date").value("2024-04-01"))
        .andExpect(jsonPath("$[0].dayMeals.size()").value(1))
        .andExpect(jsonPath("$[0].dayMeals[0].mealType").value("BREAKFAST"));
  }

  @Test
  void deleteDayByDate_ShouldDelete() throws Exception {
    createUser("john");
    Meal meal = createMeal();
    mealRepository.save(meal);
    Day d = new Day();
    d.setDate(Date.valueOf("2024-05-01"));
    d.setUser(userRepository.findByUsername("john").orElseThrow());
    DayMeal dm = new DayMeal();
    dm.setDay(d);
    dm.setMeal(meal);
    dm.setMealType(MealType.LUNCH);
    dm.setWeight(100);
    d.setDayMeals(Set.of(dm));
    dayRepository.save(d);

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/calories/days/2024-05-01?username=john"))
        .andExpect(status().isNoContent());

    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/days")).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(0));
  }

  @Test
  void deleteDayByDate_ShouldReturnNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/calories/days/2024-06-01?username=john"))
        .andExpect(status().isNotFound()).andExpect(
            jsonPath("$.message").value("Day with date 2024-06-01 not found"));
  }

  @Test
  void deleteMealFromDay_ShouldRemoveMeal() throws Exception {
    createUser("john");
    Meal meal = createMeal();
    mealRepository.save(meal);
    String add = String.format("""
        {
          "date": "2024-07-01",
          "username": "john",
          "mealId": %d,
          "weight": 100,
          "mealType": "DINNER"
        }
        """, meal.getId());
    String result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(add)).andExpect(status().isCreated()).andReturn().getResponse()
        .getContentAsString();
    Integer dayMealId = JsonPath.read(result, "$.dayMeals[0].id");
    mockMvc.perform(MockMvcRequestBuilders.delete(
            "/api/calories/days/2024-07-01/" + dayMealId + "?username=john"))
        .andExpect(status().isNoContent());

    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/days")).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].dayMeals.size()").value(0));
  }

  @Test
  void addMealToDay_ShouldReturnBadRequest_ForWeightZero() throws Exception {
    createUser("john");
    Meal meal = createMeal();
    mealRepository.save(meal);
    String json = String.format("""
        {
          "date": "2024-11-01",
          "username": "john",
          "mealId": %d,
          "weight": 0,
          "mealType": "OTHER"
        }
        """, meal.getId());
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Weight must be greater than or equal to 1"));
  }

  @Test
  void addMealToDay_ShouldReturnBadRequest_ForBlankUsername() throws Exception {
    Meal meal = createMeal();
    mealRepository.save(meal);
    String json = String.format("""
        {
          "date": "2024-12-01",
          "username": "   ",
          "mealId": %d,
          "weight": 100,
          "mealType": "SNACK"
        }
        """, meal.getId());
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Username must not be blank"));
  }
}
