package org.tcpmanager.tcpmanager.calories;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.sql.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.tcpmanager.tcpmanager.calories.intakehistory.IntakeHistory;
import org.tcpmanager.tcpmanager.calories.intakehistory.IntakeHistoryRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class IntakeHistoryTests {

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
  private IntakeHistoryRepository intakeHistoryRepository;

  @AfterEach
  void afterEach() {
    intakeHistoryRepository.deleteAll();
  }

  @Test
  void getIntakeHistoryById_ShouldReturnIntakeHistory() throws Exception {
    IntakeHistory ih = new IntakeHistory();
    ih.setUsername("testUser");
    ih.setFat(BigDecimal.valueOf(10));
    ih.setProtein(BigDecimal.valueOf(10));
    ih.setCalories(BigDecimal.valueOf(10));
    ih.setCarbs(BigDecimal.valueOf(10));
    ih.setDate(new Date(0));
    ih.setCarbsGoal(10);
    ih.setProteinGoal(10);
    ih.setFatGoal(10);
    ih.setCaloriesGoal(10);
    ih = intakeHistoryRepository.save(ih);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/intake-history/" + ih.getId()))
        .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("testUser"))
        .andExpect(jsonPath("$.fat").value(BigDecimal.valueOf(10)))
        .andExpect(jsonPath("$.protein").value(BigDecimal.valueOf(10)))
        .andExpect(jsonPath("$.calories").value(BigDecimal.valueOf(10)))
        .andExpect(jsonPath("$.carbs").value(BigDecimal.valueOf(10)))
        .andExpect(jsonPath("$.date").value("1970-01-01"))
        .andExpect(jsonPath("$.carbsGoal").value(10)).andExpect(jsonPath("$.proteinGoal").value(10))
        .andExpect(jsonPath("$.fatGoal").value(10)).andExpect(jsonPath("$.caloriesGoal").value(10));
  }

  @Test
  void getIntakeHistoryById_ShouldReturnNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/intake-history/9999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Intake history with id 9999 not found"));
  }

  @Test
  void getIntakeHistories_ShouldReturnAllIntakeHistories() throws Exception {
    IntakeHistory ih1 = new IntakeHistory();
    ih1.setUsername("testUser1");
    ih1.setFat(BigDecimal.valueOf(10));
    ih1.setProtein(BigDecimal.valueOf(10));
    ih1.setCalories(BigDecimal.valueOf(10));
    ih1.setCarbs(BigDecimal.valueOf(10));
    ih1.setDate(new Date(0));
    ih1.setCarbsGoal(10);
    ih1.setProteinGoal(10);
    ih1.setFatGoal(10);
    ih1.setCaloriesGoal(10);
    ih1 = intakeHistoryRepository.save(ih1);
    IntakeHistory ih2 = new IntakeHistory();
    ih2.setUsername("testUser2");
    ih2.setFat(BigDecimal.valueOf(20));
    ih2.setProtein(BigDecimal.valueOf(20));
    ih2.setCalories(BigDecimal.valueOf(20));
    ih2.setCarbs(BigDecimal.valueOf(20));
    ih2.setDate(new Date(0));
    ih2.setCarbsGoal(20);
    ih2.setProteinGoal(20);
    ih2.setFatGoal(20);
    ih2.setCaloriesGoal(20);
    ih2 = intakeHistoryRepository.save(ih2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/calories/intake-history"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].id").value(ih1.getId()))
        .andExpect(jsonPath("$[1].id").value(ih2.getId()));
  }
  @Test
  void updateIntakeHistoryById_ShouldUpdateIntakeHistory() throws Exception {
    IntakeHistory ih = new IntakeHistory();
    ih.setUsername("testUser");
    ih.setFat(BigDecimal.valueOf(10));
    ih.setProtein(BigDecimal.valueOf(10));
    ih.setCalories(BigDecimal.valueOf(10));
    ih.setCarbs(BigDecimal.valueOf(10));
    ih.setDate(new Date(0));
    ih.setCarbsGoal(10);
    ih.setProteinGoal(10);
    ih.setFatGoal(10);
    ih.setCaloriesGoal(10);
    ih = intakeHistoryRepository.save(ih);
    String updateJson = """
        {
          "username": "updatedUser",
          "fat": 20,
          "protein": 20,
          "calories": 20,
          "carbs": 20,
          "date": "1970-01-02",
          "carbsGoal": 20,
          "proteinGoal": 20,
          "fatGoal": 20,
          "caloriesGoal": 20
        }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/calories/intake-history/" + ih.getId())
                .contentType("application/json").content(updateJson))
        .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("updatedUser"))
        .andExpect(jsonPath("$.fat").value(BigDecimal.valueOf(20)))
        .andExpect(jsonPath("$.protein").value(BigDecimal.valueOf(20)))
        .andExpect(jsonPath("$.calories").value(BigDecimal.valueOf(20)))
        .andExpect(jsonPath("$.carbs").value(BigDecimal.valueOf(20)))
        .andExpect(jsonPath("$.date").value("1970-01-02"))
        .andExpect(jsonPath("$.carbsGoal").value(20)).andExpect(jsonPath("$.proteinGoal").value(20))
        .andExpect(jsonPath("$.fatGoal").value(20)).andExpect(jsonPath("$.caloriesGoal").value(20));
  }
  @Test
  void updateIntakeHistoryById_ShouldReturnNotFound() throws Exception {
    String updateJson = """
        {
          "username": "updatedUser",
          "fat": 20,
          "protein": 20,
          "calories": 20,
          "carbs": 20,
          "date": "1970-01-02",
          "carbsGoal": 20,
          "proteinGoal": 20,
          "fatGoal": 20,
          "caloriesGoal": 20
        }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/calories/intake-history/9999")
                .contentType("application/json").content(updateJson))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Intake history with id 9999 not found"));
  }
  @Test
  void deleteIntakeHistoryById_ShouldDeleteIntakeHistory() throws Exception {
    IntakeHistory ih = new IntakeHistory();
    ih.setUsername("testUser");
    ih.setFat(BigDecimal.valueOf(10));
    ih.setProtein(BigDecimal.valueOf(10));
    ih.setCalories(BigDecimal.valueOf(10));
    ih.setCarbs(BigDecimal.valueOf(10));
    ih.setDate(new Date(0));
    ih.setCarbsGoal(10);
    ih.setProteinGoal(10);
    ih.setFatGoal(10);
    ih.setCaloriesGoal(10);
    ih = intakeHistoryRepository.save(ih);
    mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/calories/intake-history/" + ih.getId()))
        .andExpect(status().isNoContent());
  }
  @Test
  void deleteIntakeHistoryById_ShouldReturnNotFound() throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/calories/intake-history/9999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Intake history with id 9999 not found"));
  }
}
