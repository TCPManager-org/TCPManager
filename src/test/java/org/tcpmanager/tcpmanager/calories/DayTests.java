package org.tcpmanager.tcpmanager.calories;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
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
import org.tcpmanager.tcpmanager.user.Role;
import org.tcpmanager.tcpmanager.user.User;
import org.tcpmanager.tcpmanager.user.UserRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@WithMockUser(username = "testUser", roles = "ADMIN")
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
  @Autowired
  private PasswordEncoder passwordEncoder;

  private static Meal createMeal() {
    MealIngredient mi1 = new MealIngredient();
    Meal meal = new Meal();
    meal.setName("Test Meal");

    mi1.setMeal(meal);
    Ingredient ing1 = new Ingredient();
    ing1.setName("Test Ingredient1");
    ing1.setCalories(100);
    ing1.setFats(BigDecimal.valueOf(10));
    ing1.setCarbs(BigDecimal.valueOf(20));
    ing1.setProteins(BigDecimal.valueOf(30));
    ing1.setEan("0123456789012");
    mi1.setIngredient(ing1);
    mi1.setWeight(100);

    MealIngredient mi2 = new MealIngredient();
    mi2.setMeal(meal);
    Ingredient ing2 = new Ingredient();
    ing2.setName("Test Ingredient2");
    ing2.setCalories(1000);
    ing2.setFats(BigDecimal.valueOf(100));
    ing2.setCarbs(BigDecimal.valueOf(200));
    ing2.setProteins(BigDecimal.valueOf(300));
    ing2.setEan("2345678901234");
    mi2.setIngredient(ing2);
    mi2.setWeight(100);

    meal.setMealIngredients(Set.of(mi1, mi2));
    return meal;
  }

  private static @NotNull Meal createMeal2() {
    MealIngredient mi1 = new MealIngredient();
    Meal meal2 = new Meal();
    meal2.setName("Test Meal2");

    mi1.setMeal(meal2);
    Ingredient ing1 = new Ingredient();
    ing1.setName("Test Ingredient11");
    ing1.setCalories(100);
    ing1.setFats(BigDecimal.valueOf(10));
    ing1.setCarbs(BigDecimal.valueOf(20));
    ing1.setProteins(BigDecimal.valueOf(30));
    ing1.setEan("978020137962");
    mi1.setIngredient(ing1);
    mi1.setWeight(100);

    meal2.setMealIngredients(Set.of(mi1));
    return meal2;
  }

  private User createUser() {
    User user = new User();
    user.setUsername("testUser");
    user.setPassword(passwordEncoder.encode("test"));
    user.setRole(Role.USER);
    return userRepository.save(user);
  }

  private void createUser2() {
    User user = new User();
    user.setUsername("testUser2");
    user.setPassword(passwordEncoder.encode("test"));
    user.setRole(Role.USER);
    userRepository.save(user);
  }

  @AfterEach
  void cleanup() {
    dayRepository.deleteAll();
    mealRepository.deleteAll();
    ingredientRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void addMealToDay_ShouldCreateDay() throws Exception {
    createUser();
    Meal meal = mealRepository.save(createMeal());
    String json = """
        {
          "date": "2024-01-01",
          "mealId": %d,
          "weight": 150,
          "mealType": "BREAKFAST"
        }
        """.formatted(meal.getId());

    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(json)).andExpect(status().isCreated())
        .andExpect(jsonPath("$.date").value("2024-01-01"))
        .andExpect(jsonPath("$.dayMeals.size()").value(1))
        .andExpect(jsonPath("$.dayMeals[0].weight").value(150))
        .andExpect(jsonPath("$.dayMeals[0].mealType").value("BREAKFAST"))
        .andExpect(jsonPath("$.dayMeals[0].meal.id").value(meal.getId()))
        .andExpect(jsonPath("$.dayMeals[0].meal.name").value("Test Meal"))
        .andExpect(jsonPath("$.dayMeals[0].meal.calories").value(1100.0))
        .andExpect(jsonPath("$.dayMeals[0].meal.ingredients.size()").value(2));
  }

  @Test
  void addMealToExistingDay_ShouldAppendMeal() throws Exception {
    createUser();
    Meal meal1 = mealRepository.save(createMeal());
    Meal meal2 = mealRepository.save(createMeal2());

    String first = """
        {
          "date": "2024-02-01",
          "mealId": %d,
          "weight": 100,
          "mealType": "LUNCH"
        }
        """.formatted(meal1.getId());
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
            .content(first)).andExpect(status().isCreated());

    String second = """
        {
          "date": "2024-02-01",
          "mealId": %d,
          "weight": 200,
          "mealType": "DINNER"
        }
        """.formatted(meal2.getId());
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(second)).andExpect(status().isCreated())
        .andExpect(jsonPath("$.dayMeals.size()").value(2));
  }

  @Test
  void addMealToDay_ShouldReturnNotFound_ForMissingUser() throws Exception {
    Meal meal = mealRepository.save(createMeal());
    String json = """
        {
          "date": "2024-03-01",
          "mealId": %d,
          "weight": 100,
          "mealType": "LUNCH"
        }
        """.formatted(meal.getId());
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(json)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with username testUser not found"));
  }

  @Test
  void addMealToDay_ShouldReturnNotFound_ForMissingMeal() throws Exception {
    createUser();
    String json = """
        {
          "date": "2024-03-02",
          "mealId": 999999,
          "weight": 100,
          "mealType": "SNACK"
        }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(json)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Meal with id 999999 not found"));
  }

  @Test
  void getAllDays_ShouldReturnDays() throws Exception {
    User user = createUser();
    Meal meal = mealRepository.save(createMeal());
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
        .andExpect(jsonPath("$[0].dayMeals[0].mealType").value("BREAKFAST"))
        .andExpect(jsonPath("$[0].dayMeals[0].meal.name").value("Test Meal"))
        .andExpect(jsonPath("$[0].dayMeals[0].meal.calories").value(1100.0));
  }

  @Test
  void deleteDayByDate_ShouldDelete() throws Exception {
    createUser();
    Meal meal = mealRepository.save(createMeal());
    Day d = new Day();
    d.setDate(Date.valueOf("2024-05-01"));
    d.setUser(userRepository.findByUsername("testUser").orElseThrow());
    DayMeal dm = new DayMeal();
    dm.setDay(d);
    dm.setMeal(meal);
    dm.setMealType(MealType.LUNCH);
    dm.setWeight(100);
    d.setDayMeals(Set.of(dm));
    dayRepository.save(d);

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/calories/days/2024-05-01"))
        .andExpect(status().isNoContent());

    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/days")).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(0));
  }

  @Test
  void deleteDayByDate_ShouldReturnNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/calories/days/2024-06-01"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Day with date 2024-06-01 not found"));
  }

  @Test
  void deleteMealFromDay_ShouldRemoveMeal() throws Exception {
    createUser();
    Meal meal = mealRepository.save(createMeal());
    String add = """
        {
          "date": "2024-07-01",
          "mealId": %d,
          "weight": 100,
          "mealType": "DINNER"
        }
        """.formatted(meal.getId());
    String result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(add)).andExpect(status().isCreated()).andReturn().getResponse()
        .getContentAsString();
    Integer dayMealId = JsonPath.read(result, "$.dayMeals[0].id");

    mockMvc.perform(MockMvcRequestBuilders.delete(
            "/api/calories/days/2024-07-01/" + dayMealId))
        .andExpect(status().isNoContent());

    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/days")).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].dayMeals.size()").value(0));
  }

  @Test
  void addMealToDay_ShouldReturnBadRequest_ForWeightZero() throws Exception {
    createUser();
    Meal meal = mealRepository.save(createMeal());
    String json = """
        {
          "date": "2024-11-01",
          "mealId": %d,
          "weight": 0,
          "mealType": "OTHER"
        }
        """.formatted(meal.getId());
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Weight must be greater than or equal to 1"));
  }

  @Test
  void deleteMealFromDay_ShouldReturnNotFound_ForMissingDayMeal() throws Exception {
    createUser();
    Meal meal = mealRepository.save(createMeal());
    String add = """
        {
          "date": "2024-08-01",
          "mealId": %d,
          "weight": 100,
          "mealType": "SNACK"
        }
        """.formatted(meal.getId());
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
            .content(add)).andExpect(status().isCreated());

    mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/calories/days/2024-08-01/999999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("DayMeal with id 999999 and 2024-08-01 not found"));
  }

  @Test
  void updateMealInDay_ShouldUpdateWeightAndMealType() throws Exception {
    createUser();
    Meal meal = mealRepository.save(createMeal());
    String add = """
        {
          "date": "2024-09-01",
          "mealId": %d,
          "weight": 100,
          "mealType": "BREAKFAST"
        }
        """.formatted(meal.getId());
    String result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(add)).andExpect(status().isCreated()).andReturn().getResponse()
        .getContentAsString();
    Integer dayMealId = JsonPath.read(result, "$.dayMeals[0].id");

    String updateJson = """
        {
          "weight": 200,
          "mealType": "DINNER"
        }
        """;
    mockMvc.perform(MockMvcRequestBuilders.patch(
                "/api/calories/days/2024-09-01/" + dayMealId)
            .contentType("application/json").content(updateJson)).andExpect(status().isOk())
        .andExpect(jsonPath("$.dayMeals[0].weight").value(200))
        .andExpect(jsonPath("$.dayMeals[0].mealType").value("DINNER"))
        .andExpect(jsonPath("$.dayMeals[0].meal.calories").value(1100.0));
  }

  @Test
  void updateMealInDay_ShouldUpdateMeal() throws Exception {
    createUser();
    Meal meal = mealRepository.save(createMeal());
    Meal newMeal = mealRepository.save(createMeal2());
    String add = """
        {
          "date": "2024-10-01",
          "mealId": %d,
          "weight": 100,
          "mealType": "LUNCH"
        }
        """.formatted(meal.getId());
    String result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(add)).andExpect(status().isCreated()).andReturn().getResponse()
        .getContentAsString();
    Integer dayMealId = JsonPath.read(result, "$.dayMeals[0].id");

    String updateJson = """
        {
          "mealId": %d
        }
        """.formatted(newMeal.getId());
    mockMvc.perform(MockMvcRequestBuilders.patch(
                "/api/calories/days/2024-10-01/" + dayMealId)
            .contentType("application/json").content(updateJson)).andExpect(status().isOk())
        .andExpect(jsonPath("$.dayMeals[0].meal.name").value("Test Meal2"))
        .andExpect(jsonPath("$.dayMeals[0].meal.calories").value(100.0));
  }

  @Test
  void updateMealInDay_ShouldReturnNotFound_ForMissingMeal() throws Exception {
    createUser();
    Meal meal = mealRepository.save(createMeal());
    String add = """
        {
          "date": "2024-12-01",
          "mealId": %d,
          "weight": 100,
          "mealType": "OTHER"
        }
        """.formatted(meal.getId());
    String result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(add)).andExpect(status().isCreated()).andReturn().getResponse()
        .getContentAsString();
    Integer dayMealId = JsonPath.read(result, "$.dayMeals[0].id");

    String updateJson = """
        {
          "mealId": 888888
        }
        """;
    mockMvc.perform(MockMvcRequestBuilders.patch(
                "/api/calories/days/2024-12-01/" + dayMealId)
            .contentType("application/json").content(updateJson)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Meal with id 888888 not found"));
  }

  @Test
  void deleteMealFromDay_ShouldReturnBadRequest_ForDayNotBelongingToUser() throws Exception {
    createUser();
    createUser2();
    Meal meal = mealRepository.save(createMeal());
    String add = """
        {
          "date": "2024-08-15",
          "mealId": %d,
          "weight": 100,
          "mealType": "SNACK"
        }
        """.formatted(meal.getId());
    String result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/days").contentType("application/json")
                .content(add)).andExpect(status().isCreated()).andReturn().getResponse()
        .getContentAsString();
    Integer dayMealId = JsonPath.read(result, "$.dayMeals[0].id");

    mockMvc.perform(MockMvcRequestBuilders.delete(
                "/api/calories/days/2024-08-15/" + dayMealId + "?username=jane")
            .with(user("admin").password("pass").roles(
                "ADMIN")))
        .andExpect(status().isBadRequest()).andExpect(
            jsonPath("$.message").value("Day with date 2024-08-15 does not belong to user admin"));
  }

  @Test
  void deleteDayByDate_ShouldReturnBadRequest_ForDayNotBelongingToUser() throws Exception {
    createUser();
    createUser2();
    Meal meal = mealRepository.save(createMeal());
    Day d = new Day();
    d.setDate(Date.valueOf("2024-05-15"));
    d.setUser(userRepository.findByUsername("testUser").orElseThrow());
    DayMeal dm = new DayMeal();
    dm.setDay(d);
    dm.setMeal(meal);
    dm.setMealType(MealType.LUNCH);
    dm.setWeight(100);
    d.setDayMeals(Set.of(dm));
    dayRepository.save(d);

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/calories/days/2024-05-15?username=jane")
            .with(user("admin").password("pass").roles(
                "ADMIN")))
        .andExpect(status().isBadRequest()).andExpect(
            jsonPath("$.message").value("Day with date 2024-05-15 does not belong to user admin"));
  }

  @Test
  void updateMealInDay_ShouldReturnNotFound_ForMissingDay() throws Exception {
    createUser();
    String patch = """
        {
          "weight": 250
        }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/calories/days/2025-01-01/12345")
                .contentType("application/json").content(patch)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Day with date 2025-01-01 not found"));
  }
}
