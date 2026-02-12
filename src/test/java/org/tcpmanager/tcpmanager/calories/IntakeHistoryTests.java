package org.tcpmanager.tcpmanager.calories;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.sql.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.tcpmanager.tcpmanager.statistics.intakehistory.IntakeHistory;
import org.tcpmanager.tcpmanager.statistics.intakehistory.IntakeHistoryRepository;
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
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @AfterEach
  void afterEach() {
    intakeHistoryRepository.deleteAll();
    userRepository.deleteAll();
  }

  private User createUser() {
    User user = new User();
    user.setUsername("testUser");
    user.setPassword(passwordEncoder.encode("testPassword"));
    user.setRole(Role.ADMIN);
    user = userRepository.save(user);
    return user;
  }

  @Test
  void getIntakeHistoryById_ShouldReturnIntakeHistory() throws Exception {
    User user = createUser();
    IntakeHistory ih = new IntakeHistory();
    ih.setUser(user);
    ih.setFat(BigDecimal.valueOf(10));
    ih.setProtein(BigDecimal.valueOf(11));
    ih.setCalories(BigDecimal.valueOf(12));
    ih.setCarbs(BigDecimal.valueOf(13));
    ih.setDate(new Date(0));
    ih.setCarbsGoal(14);
    ih.setProteinGoal(15);
    ih.setFatGoal(16);
    ih.setCaloriesGoal(17);
    ih = intakeHistoryRepository.save(ih);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics/intake-history/" + ih.getId()))
        .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("testUser"))
        .andExpect(jsonPath("$.fat").value(BigDecimal.valueOf(10)))
        .andExpect(jsonPath("$.protein").value(BigDecimal.valueOf(11)))
        .andExpect(jsonPath("$.calories").value(BigDecimal.valueOf(12)))
        .andExpect(jsonPath("$.carbs").value(BigDecimal.valueOf(13)))
        .andExpect(jsonPath("$.date").value("1970-01-01"))
        .andExpect(jsonPath("$.carbsGoal").value(14)).andExpect(jsonPath("$.proteinGoal").value(15))
        .andExpect(jsonPath("$.fatGoal").value(16)).andExpect(jsonPath("$.caloriesGoal").value(17));
  }

  @Test
  void getIntakeHistoryById_ShouldReturnNotFound() throws Exception {
    createUser();
    mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics/intake-history/9999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Intake history with id 9999 not found"));
  }

  @Test
  void getIntakeHistoriesByUsername_ShouldReturnAllIntakeHistories() throws Exception {
    User user = createUser();
    IntakeHistory ih1 = new IntakeHistory();
    ih1.setUser(user);
    ih1.setFat(BigDecimal.valueOf(10));
    ih1.setProtein(BigDecimal.valueOf(11));
    ih1.setCalories(BigDecimal.valueOf(12));
    ih1.setCarbs(BigDecimal.valueOf(13));
    ih1.setDate(new Date(0));
    ih1.setCarbsGoal(14);
    ih1.setProteinGoal(15);
    ih1.setFatGoal(16);
    ih1.setCaloriesGoal(17);
    ih1 = intakeHistoryRepository.save(ih1);
    IntakeHistory ih2 = new IntakeHistory();
    ih2.setUser(ih1.getUser());
    ih2.setFat(BigDecimal.valueOf(20));
    ih2.setProtein(BigDecimal.valueOf(21));
    ih2.setCalories(BigDecimal.valueOf(22));
    ih2.setCarbs(BigDecimal.valueOf(23));
    ih2.setDate(new Date(86400001));
    ih2.setCarbsGoal(24);
    ih2.setProteinGoal(25);
    ih2.setFatGoal(26);
    ih2.setCaloriesGoal(27);
    ih2 = intakeHistoryRepository.save(ih2);
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/statistics/intake-history"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].id").value(ih1.getId()))
        .andExpect(jsonPath("$[1].id").value(ih2.getId()));
  }

  @Test
  void deleteIntakeHistoryById_ShouldDeleteIntakeHistory() throws Exception {
    User user = createUser();
    IntakeHistory ih = new IntakeHistory();
    ih.setUser(user);
    ih.setFat(BigDecimal.valueOf(10));
    ih.setProtein(BigDecimal.valueOf(11));
    ih.setCalories(BigDecimal.valueOf(12));
    ih.setCarbs(BigDecimal.valueOf(13));
    ih.setDate(new Date(0));
    ih.setCarbsGoal(14);
    ih.setProteinGoal(15);
    ih.setFatGoal(16);
    ih.setCaloriesGoal(17);
    ih = intakeHistoryRepository.save(ih);
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/statistics/intake-history/" + ih.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteIntakeHistoryById_ShouldReturnNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/statistics/intake-history/9999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Intake history with id 9999 not found"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"fat", "protein", "calories", "carbs", "caloriesGoal", "proteinGoal",
      "fatGoal", "carbsGoal"})
  void updateIntakeHistoryById_ShouldUpdateIntakeHistory(String value) throws Exception {
    User user = createUser();
    IntakeHistory ih = new IntakeHistory();
    ih.setUser(user);
    ih.setFat(BigDecimal.valueOf(10));
    ih.setProtein(BigDecimal.valueOf(11));
    ih.setCalories(BigDecimal.valueOf(12));
    ih.setCarbs(BigDecimal.valueOf(13));
    ih.setDate(new Date(0));
    ih.setCarbsGoal(14);
    ih.setProteinGoal(15);
    ih.setFatGoal(16);
    ih.setCaloriesGoal(17);
    ih = intakeHistoryRepository.save(ih);
    String patchJson = """
        {
          "%s": 20
        }
        """.formatted(value);
    mockMvc.perform(MockMvcRequestBuilders.patch("/api/statistics/intake-history/" + ih.getId())
            .contentType("application/json").content(patchJson)).andExpect(status().isOk())
        .andExpect(jsonPath("$." + value).value(20));
  }

  @Test
  void addIntakeHistory_ShouldAddIntakeHistory() throws Exception {
    createUser();
    String postJson = """
        {
          "fat": 10,
          "protein": 11,
          "calories": 12,
          "carbs": 13,
          "date": "1970-01-01",
          "carbsGoal": 14,
          "proteinGoal": 15,
          "fatGoal": 16,
          "caloriesGoal": 17
        }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/statistics/intake-history")
                .contentType("application/json")
                .content(postJson)).andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("testUser")).andExpect(jsonPath("$.fat").value(10))
        .andExpect(jsonPath("$.protein").value(11)).andExpect(jsonPath("$.calories").value(12))
        .andExpect(jsonPath("$.carbs").value(13)).andExpect(jsonPath("$.date").value("1970-01-01"))
        .andExpect(jsonPath("$.carbsGoal").value(14)).andExpect(jsonPath("$.proteinGoal").value(15))
        .andExpect(jsonPath("$.fatGoal").value(16)).andExpect(jsonPath("$.caloriesGoal").value(17));
  }

  @Test
  void addIntakeHistory_InvalidUsername() throws Exception {
    String postJson = """
        {
          "fat": 10,
          "protein": 11,
          "calories": 12,
          "carbs": 13,
          "date": "1970-01-01",
          "carbsGoal": 14,
          "proteinGoal": 15,
          "fatGoal": 16,
          "caloriesGoal": 17
        }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/statistics/intake-history")
                .with(user("admin").password("pass").roles("ADMIN"))
                .contentType("application/json")
                .content(postJson)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with username admin not found"));
  }

  @Test
  void addIntakeHistory_NonUniqueDate() throws Exception {
    User user = createUser();
    IntakeHistory ih = new IntakeHistory();
    ih.setUser(user);
    ih.setFat(BigDecimal.valueOf(10));
    ih.setProtein(BigDecimal.valueOf(11));
    ih.setCalories(BigDecimal.valueOf(12));
    ih.setCarbs(BigDecimal.valueOf(13));
    ih.setDate(new Date(0));
    ih.setCarbsGoal(14);
    ih.setProteinGoal(15);
    ih.setFatGoal(16);
    ih.setCaloriesGoal(17);
    intakeHistoryRepository.save(ih);
    String postJson = """
        {
          "username": "testUser",
          "fat": 10,
          "protein": 11,
          "calories": 12,
          "carbs": 13,
          "date": "1970-01-01",
          "carbsGoal": 14,
          "proteinGoal": 15,
          "fatGoal": 16,
          "caloriesGoal": 17
        }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/statistics/intake-history")
                .contentType("application/json")
                .content(postJson)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Date must be unique"));
  }

  @Test
  void updateIntakeHistoryById_ShouldReturnNotFound() throws Exception {
    String patchJson = """
        {
          "fat": 20
        }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/statistics/intake-history/9999")
                .contentType("application/json")
                .content(patchJson)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Intake history with id 9999 not found"));
  }

  @Test
  void deleteIntakeHistoryByUsername_ShouldDeleteIntakeHistories() throws Exception {
    User user = createUser();
    IntakeHistory ih1 = new IntakeHistory();
    ih1.setUser(user);
    ih1.setFat(BigDecimal.valueOf(10));
    ih1.setProtein(BigDecimal.valueOf(11));
    ih1.setCalories(BigDecimal.valueOf(12));
    ih1.setCarbs(BigDecimal.valueOf(13));
    ih1.setDate(new Date(0));
    ih1.setCarbsGoal(14);
    ih1.setProteinGoal(15);
    ih1.setFatGoal(16);
    ih1.setCaloriesGoal(17);
    intakeHistoryRepository.save(ih1);
    IntakeHistory ih2 = new IntakeHistory();
    ih2.setUser(user);
    ih2.setFat(BigDecimal.valueOf(20));
    ih2.setProtein(BigDecimal.valueOf(21));
    ih2.setCalories(BigDecimal.valueOf(22));
    ih2.setCarbs(BigDecimal.valueOf(23));
    ih2.setDate(new Date(1));
    ih2.setCarbsGoal(24);
    ih2.setProteinGoal(25);
    ih2.setFatGoal(26);
    ih2.setCaloriesGoal(27);
    intakeHistoryRepository.save(ih2);
    mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/statistics/intake-history")
                .param("username", "testUser"))
        .andExpect(status().isNoContent());
    Assertions.assertTrue(intakeHistoryRepository.getAllByUserUsername("testUser").isEmpty());
  }
}
