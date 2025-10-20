package org.tcpmanager.tcpmanager.calories;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.tcpmanager.tcpmanager.calories.ingredient.Ingredient;
import org.tcpmanager.tcpmanager.calories.ingredient.IngredientRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class IngredientTests {

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
  private IngredientRepository ingredientRepository;

  @Test
  void validation_EanIsShorter() throws Exception {
    String json = """
          {
            "name": "Name",
            "calories": 1,
            "fats": 1,
            "carbs": 1,
            "proteins": 1,
            "ean": "01234567"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Ean must be 13 characters long"));
  }

  @Test
  void validation_EanIsBlank() throws Exception {
    String json = """
          {
            "name": "Name",
            "calories": 1,
            "fats": 1,
            "carbs": 1,
            "proteins": 1,
            "ean": "             "
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Ean must not be blank"));
  }

  @Test
  void validation_EanHasAlpha() throws Exception {
    String json = """
          {
            "name": "Name",
            "calories": 1,
            "fats": 1,
            "carbs": 1,
            "proteins": 1,
            "ean": "01234567ABCDE"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("EAN must contain only digits"));
  }

  @Test
  void validation_EanWrongChecksum() throws Exception {
    String json = """
          {
            "name": "Name",
            "calories": 1,
            "fats": 1,
            "carbs": 1,
            "proteins": 1,
            "ean": "0123456789016"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("EAN is not valid"));
  }

  @AfterEach
  void afterEach() {
    ingredientRepository.deleteAll();
  }

  @Test
  void validation_NameIsBlank() throws Exception {
    String json = """
          {
            "name": " ",
            "calories": 1,
            "fats": 1,
            "carbs": 1,
            "proteins": 1,
            "ean": "0123456789012"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Name must not be blank"));
  }

  @Test
  void validation_CaloriesAreNull() throws Exception {
    String json = """
          {
            "name": "Name",
            "fats": 1,
            "carbs": 1,
            "proteins": 1,
            "ean": "0123456789012"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Calories must not be null"));
  }

  @Test
  void validation_CaloriesAre0() throws Exception {
    String json = """
          {
            "name": "Name",
            "calories": 0,
            "fats": 1,
            "carbs": 1,
            "proteins": 1,
            "ean": "0123456789012"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Calories must be greater than 0"));
  }

  @Test
  void validation_FatsAreNull() throws Exception {
    String json = """
          {
            "name": "Name",
            "calories": 1,
            "carbs": 1,
            "proteins": 1,
            "ean": "0123456789012"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Fats must not be null"));
  }

  @Test
  void validation_FatsAre0() throws Exception {
    String json = """
          {
            "name": "Name",
            "calories": 1,
            "fats": 0,
            "carbs": 1,
            "proteins": 1,
            "ean": "0123456789012"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Fats must be greater than 0"));
  }

  @Test
  void validation_CarbsAreNull() throws Exception {
    String json = """
          {
            "name": "Name",
            "calories": 1,
            "fats": 1,
            "proteins": 1,
            "ean": "0123456789012"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Carbs must not be null"));
  }

  @Test
  void validation_CarbsAre0() throws Exception {
    String json = """
          {
            "name": "Name",
            "calories": 1,
            "fats": 1,
            "carbs": 0,
            "proteins": 1,
            "ean": "0123456789012"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Carbs must be greater than 0"));
  }

  @Test
  void validation_ProteinsAreNull() throws Exception {
    String json = """
          {
            "name": "Name",
            "calories": 1,
            "fats": 1,
            "carbs": 1,
            "ean": "0123456789012"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Proteins must not be null"));
  }

  @Test
  void validation_ProteinsAre0() throws Exception {
    String json = """
          {
            "name": "Name",
            "calories": 1,
            "fats": 1,
            "carbs": 1,
            "proteins": 0,
            "ean": "0123456789012"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Proteins must be greater than 0"));
  }

  @Test
  void getIngredients_ShouldReturnAllIngredients() throws Exception {
    Ingredient ingredient = new Ingredient();
    ingredient.setName("Test Ingredient");
    ingredient.setEan("1234567890123");
    ingredient.setCalories(BigDecimal.valueOf(1));
    ingredient.setFats(BigDecimal.valueOf(2));
    ingredient.setCarbs(BigDecimal.valueOf(3));
    ingredient.setProteins(BigDecimal.valueOf(4));
    ingredient = ingredientRepository.save(ingredient);

    Ingredient ingredient2 = new Ingredient();
    ingredient2.setName("Test Ingredient2");
    ingredient2.setEan("2345678901234");
    ingredient2.setCalories(BigDecimal.valueOf(10));
    ingredient2.setFats(BigDecimal.valueOf(20));
    ingredient2.setCarbs(BigDecimal.valueOf(30));
    ingredient2.setProteins(BigDecimal.valueOf(40));
    ingredient2 = ingredientRepository.save(ingredient2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/ingredients"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].id").value(ingredient.getId()))
        .andExpect(jsonPath("$[0].name").value("Test Ingredient"))
        .andExpect(jsonPath("$[0].ean").value("1234567890123"))
        .andExpect(jsonPath("$[0].calories").value(1)).andExpect(jsonPath("$[0].fats").value(2))
        .andExpect(jsonPath("$[0].carbs").value(3)).andExpect(jsonPath("$[0].proteins").value(4))
        .andExpect(jsonPath("$[1].id").value(ingredient2.getId()))
        .andExpect(jsonPath("$[1].name").value("Test Ingredient2"))
        .andExpect(jsonPath("$[1].ean").value("2345678901234"))
        .andExpect(jsonPath("$[1].calories").value(10)).andExpect(jsonPath("$[1].fats").value(20))
        .andExpect(jsonPath("$[1].carbs").value(30)).andExpect(jsonPath("$[1].proteins").value(40));
  }

  @Test
  void getIngredient_ShouldReturnNotFound() throws Exception {
    Ingredient ingredient = new Ingredient();
    ingredient.setName("Test Ingredient");
    ingredient.setEan("1234567890123");
    ingredient.setCalories(BigDecimal.valueOf(1));
    ingredient.setFats(BigDecimal.valueOf(2));
    ingredient.setCarbs(BigDecimal.valueOf(3));
    ingredient.setProteins(BigDecimal.valueOf(4));
    ingredientRepository.save(ingredient);

    Ingredient ingredient2 = new Ingredient();
    ingredient2.setName("Test Ingredient2");
    ingredient2.setEan("2345678901234");
    ingredient2.setCalories(BigDecimal.valueOf(10));
    ingredient2.setFats(BigDecimal.valueOf(20));
    ingredient2.setCarbs(BigDecimal.valueOf(30));
    ingredient2.setProteins(BigDecimal.valueOf(40));
    ingredientRepository.save(ingredient2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/ingredients/123"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Ingredient with id 123 not found"));
  }

  @Test
  void getIngredient_ShouldReturnIngredient() throws Exception {
    Ingredient ingredient = new Ingredient();
    ingredient.setName("Test Ingredient");
    ingredient.setEan("1234567890123");
    ingredient.setCalories(BigDecimal.valueOf(1));
    ingredient.setFats(BigDecimal.valueOf(2));
    ingredient.setCarbs(BigDecimal.valueOf(3));
    ingredient.setProteins(BigDecimal.valueOf(4));
    ingredientRepository.save(ingredient);

    Ingredient ingredient2 = new Ingredient();
    ingredient2.setName("Test Ingredient2");
    ingredient2.setEan("2345678901234");
    ingredient2.setCalories(BigDecimal.valueOf(10));
    ingredient2.setFats(BigDecimal.valueOf(20));
    ingredient2.setCarbs(BigDecimal.valueOf(30));
    ingredient2.setProteins(BigDecimal.valueOf(40));
    ingredientRepository.save(ingredient2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/ingredients/" + ingredient.getId()))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(ingredient.getId()))
        .andExpect(jsonPath("$.name").value("Test Ingredient"))
        .andExpect(jsonPath("$.ean").value("1234567890123"))
        .andExpect(jsonPath("$.calories").value(1)).andExpect(jsonPath("$.fats").value(2))
        .andExpect(jsonPath("$.carbs").value(3)).andExpect(jsonPath("$.proteins").value(4));
  }

  @Test
  void addIngredient_ShouldReturnCreated() throws Exception {
    String json = """
          {
            "name": "New Ingredient",
            "calories": 1,
            "fats": 2,
            "carbs": 3,
            "proteins": 4,
            "ean": "0123456789012"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.name").value("New Ingredient"))
        .andExpect(jsonPath("$.ean").value("0123456789012"))
        .andExpect(jsonPath("$.calories").value(1)).andExpect(jsonPath("$.fats").value(2))
        .andExpect(jsonPath("$.carbs").value(3)).andExpect(jsonPath("$.proteins").value(4));
  }

  @Test
  void addIngredient_ShouldReturnCreatedWithoutEan() throws Exception {
    String json = """
          {
            "name": "New Ingredient",
            "calories": 1,
            "fats": 2,
            "carbs": 3,
            "proteins": 4
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.name").value("New Ingredient"))
        .andExpect(jsonPath("$.ean").doesNotExist()).andExpect(jsonPath("$.calories").value(1))
        .andExpect(jsonPath("$.fats").value(2)).andExpect(jsonPath("$.carbs").value(3))
        .andExpect(jsonPath("$.proteins").value(4));
  }

  @Test
  void deleteIngredient_ShouldReturnNoContent() throws Exception {
    Ingredient ingredient = new Ingredient();
    ingredient.setName("Test Ingredient");
    ingredient.setEan("1234567890123");
    ingredient.setCalories(BigDecimal.valueOf(1));
    ingredient.setFats(BigDecimal.valueOf(2));
    ingredient.setCarbs(BigDecimal.valueOf(3));
    ingredient.setProteins(BigDecimal.valueOf(4));
    ingredient = ingredientRepository.save(ingredient);
    mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/calories/ingredients/" + ingredient.getId()))
        .andExpect(status().isNoContent());
    Assertions.assertEquals(0, ingredientRepository.count());
  }

  @Test
  void deleteIngredient_ShouldReturnNotFound() throws Exception {
    Ingredient ingredient = new Ingredient();
    ingredient.setName("Test Ingredient");
    ingredient.setEan("1234567890123");
    ingredient.setCalories(BigDecimal.valueOf(1));
    ingredient.setFats(BigDecimal.valueOf(2));
    ingredient.setCarbs(BigDecimal.valueOf(3));
    ingredient.setProteins(BigDecimal.valueOf(4));
    ingredientRepository.save(ingredient);
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/calories/ingredients/123"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Ingredient with id 123 not found"));
    Assertions.assertEquals(1, ingredientRepository.count());
  }

  @Test
  void updateIngredient_Name() throws Exception {
    Ingredient ingredient = new Ingredient();
    ingredient.setName("Test Ingredient");
    ingredient.setEan("1234567890123");
    ingredient.setCalories(BigDecimal.valueOf(1));
    ingredient.setFats(BigDecimal.valueOf(2));
    ingredient.setCarbs(BigDecimal.valueOf(3));
    ingredient.setProteins(BigDecimal.valueOf(4));
    ingredient = ingredientRepository.save(ingredient);
    String json = """
          {
            "name": "Updated Ingredient"
          }
        """;
    mockMvc.perform(MockMvcRequestBuilders.patch("/api/calories/ingredients/" + ingredient.getId())
            .contentType("application/json").content(json)).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(ingredient.getId()))
        .andExpect(jsonPath("$.name").value("Updated Ingredient"));
  }

  @Test
  void updateIngredient_ShouldThrowNameIsBlank() throws Exception {
    Ingredient ingredient = new Ingredient();
    ingredient.setName("Test Ingredient");
    ingredient.setEan("1234567890123");
    ingredient.setCalories(BigDecimal.valueOf(1));
    ingredient.setFats(BigDecimal.valueOf(2));
    ingredient.setCarbs(BigDecimal.valueOf(3));
    ingredient.setProteins(BigDecimal.valueOf(4));
    ingredient = ingredientRepository.save(ingredient);
    String json = """
          {
            "name": " "
          }
        """;

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/calories/ingredients/" + ingredient.getId())
            .contentType("application/json").content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Name must not be blank"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"fats", "calories", "carbs", "proteins"})
  void updateIngredient_Fats(String value) throws Exception {
    Ingredient ingredient = new Ingredient();
    ingredient.setName("Test Ingredient");
    ingredient.setEan("1234567890123");
    ingredient.setCalories(BigDecimal.valueOf(1));
    ingredient.setFats(BigDecimal.valueOf(2));
    ingredient.setCarbs(BigDecimal.valueOf(3));
    ingredient.setProteins(BigDecimal.valueOf(4));
    ingredient = ingredientRepository.save(ingredient);
    String json = """
          {
            "%s": 10
          }
        """.formatted(value);
    mockMvc.perform(MockMvcRequestBuilders.patch("/api/calories/ingredients/" + ingredient.getId())
            .contentType("application/json").content(json)).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(ingredient.getId()))
        .andExpect(jsonPath("$." + value).value(10));
  }

  @Test
  void updateIngredient_Ean() throws Exception {
    Ingredient ingredient = new Ingredient();
    ingredient.setName("Test Ingredient");
    ingredient.setEan("1234567890123");
    ingredient.setCalories(BigDecimal.valueOf(1));
    ingredient.setFats(BigDecimal.valueOf(2));
    ingredient.setCarbs(BigDecimal.valueOf(3));
    ingredient.setProteins(BigDecimal.valueOf(4));
    ingredient = ingredientRepository.save(ingredient);
    String json = """
          {
            "ean": "0123456789104"
          }
        """;
    mockMvc.perform(MockMvcRequestBuilders.patch("/api/calories/ingredients/" + ingredient.getId())
            .contentType("application/json").content(json)).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(ingredient.getId()))
        .andExpect(jsonPath("$.ean").value("0123456789104"));
  }

  @Test
  void updateIngredient_ShouldReturnNotFound() throws Exception {
    Ingredient ingredient = new Ingredient();
    ingredient.setName("Test Ingredient");
    ingredient.setEan("1234567890123");
    ingredient.setCalories(BigDecimal.valueOf(1));
    ingredient.setFats(BigDecimal.valueOf(2));
    ingredient.setCarbs(BigDecimal.valueOf(3));
    ingredient.setProteins(BigDecimal.valueOf(4));
    ingredientRepository.save(ingredient);
    String json = """
          {
            "name": "Updated Ingredient"
          }
        """;
    mockMvc.perform(MockMvcRequestBuilders.patch("/api/calories/ingredients/123")
            .contentType("application/json").content(json)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Ingredient with id 123 not found"));
  }

  @Test
  void addIngredient_ShouldReturnBadRequest_WhenNameIsNull() throws Exception {
    String json = """
          {
            "calories": 1,
            "fats": 1,
            "carbs": 1,
            "proteins": 1,
            "ean": "0123456789012"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/calories/ingredients").contentType("application/json")
                .content(json)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Name must not be blank"));
  }
}