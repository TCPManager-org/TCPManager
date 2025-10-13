package org.tcpmanager.tcpmanager.calories;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.tcpmanager.tcpmanager.calories.meal.Meal;
import org.tcpmanager.tcpmanager.calories.meal.MealRepository;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealPatch;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealRequest;
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

  @AfterEach
  void afterEach() {
    mealRepository.deleteAll();
  }

  @Test
  void getMeals_ShouldReturnAllMeals() throws Exception {
    Meal meal = new Meal();
    meal.setName("Test Meal");
    meal = mealRepository.save(meal);
    Meal meal2 = new Meal();
    meal2.setName("Test Meal2");
    meal2 = mealRepository.save(meal2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/meals")).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[0].id").value(meal.getId()))
        .andExpect(jsonPath("$[0].name").value("Test Meal"))
        .andExpect(jsonPath("$[1].id").value(meal2.getId()))
        .andExpect(jsonPath("$[1].name").value("Test Meal2"));
  }

  @Test
  void getMeal_ShouldReturnNotFound() throws Exception {
    Meal meal = new Meal();
    meal.setName("Test Meal");
    mealRepository.save(meal);
    Meal meal2 = new Meal();
    meal2.setName("Test Meal2");
    mealRepository.save(meal2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/meals/123"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Meal with id 123 not found"));
  }

  @Test
  void getMeal_ShouldReturnMeal() throws Exception {
    Meal meal = new Meal();
    meal.setName("Test Meal");
    meal = mealRepository.save(meal);
    Meal meal2 = new Meal();
    meal2.setName("Test Meal2");
    mealRepository.save(meal2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/meals/" + meal.getId()))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(meal.getId()))
        .andExpect(jsonPath("$.name").value("Test Meal"));
  }

  @Test
  void addMeal_ShouldReturnCreated() throws Exception {
    MealRequest mealRequest = new MealRequest("New Meal");
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/meals").contentType("application/json")
                .content(asJsonString(mealRequest))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber()).andExpect(jsonPath("$.name").value("New Meal"));
  }

  @Test
  void deleteMeal_ShouldReturnNoContent() throws Exception {
    Meal meal = new Meal();
    meal.setName("Test Meal");
    meal = mealRepository.save(meal);
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/calories/meals/" + meal.getId()))
        .andExpect(status().isNoContent());
    Assertions.assertEquals(0, mealRepository.count());
  }

  @Test
  void deleteMeal_ShouldReturnNotFound() throws Exception {
    Meal meal = new Meal();
    meal.setName("Test Meal");
    mealRepository.save(meal);
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/calories/meals/123"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Meal with id 123 not found"));
    Assertions.assertEquals(1, mealRepository.count());
  }

  @Test
  void updateMeal_ShouldReturnUpdatedMeal() throws Exception {
    Meal meal = new Meal();
    meal.setName("Test Meal");
    meal = mealRepository.save(meal);
    MealPatch mealPatch = new MealPatch("Updated Meal");
    mockMvc.perform(MockMvcRequestBuilders.patch("/api/calories/meals/" + meal.getId())
            .contentType("application/json").content(asJsonString(mealPatch)))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(meal.getId()))
        .andExpect(jsonPath("$.name").value("Updated Meal"));
    Meal updatedMeal = mealRepository.findById(meal.getId()).orElseThrow();
    Assertions.assertEquals("Updated Meal", updatedMeal.getName());
  }

  @Test
  void updateMeal_ShouldReturnNotFound() throws Exception {
    Meal meal = new Meal();
    meal.setName("Test Meal");
    mealRepository.save(meal);
    MealPatch mealPatch = new MealPatch("Updated Meal");
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/calories/meals/123").contentType("application/json")
                .content(asJsonString(mealPatch))).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Meal with id 123 not found"));
  }

  @Test
  void addMeal_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
    MealRequest mealRequest = new MealRequest(" ");
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/meals").contentType("application/json")
                .content(asJsonString(mealRequest))).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Name must not be blank"));
  }

  @Test
  void addMeal_ShouldReturnBadRequest_WhenNameIsNull() throws Exception {
    MealRequest mealRequest = new MealRequest(null);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/meals").contentType("application/json")
                .content(asJsonString(mealRequest))).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Name must not be blank"));
  }

  private String asJsonString(Object object) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(object);
  }
}