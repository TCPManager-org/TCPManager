package org.tcpmanager.tcpmanager.calories;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
class UserTests {

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
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @AfterEach
  void afterEach() {
    userRepository.deleteAll();
  }

  private User createUser() {
    User user = new User();
    user.setUsername("testUser");
    user.setPassword(passwordEncoder.encode("password"));
    user.setRole(Role.ADMIN);
    return userRepository.save(user);
  }

  private User createUser2() {
    User user = new User();
    user.setUsername("testUser2");
    user.setPassword(passwordEncoder.encode("password"));
    user.setRole(Role.ADMIN);
    return userRepository.save(user);
  }

  @Test
  void invalidUsername_NotUnique() throws Exception {
    createUser();
    String json = """
          {
            "username": "testUser",
            "password": "password",
            "role": "USER"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users").contentType("application/json").content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Username testUser is already taken"));
  }

  @Test
  void invalidUsername_NotAlphanumeric() throws Exception {
    String json = """
          {
            "username": "testUser!",
            "password": "password",
            "role": "USER"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users").contentType("application/json").content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Username can only contain letters and digits"));
  }

  @Test
  void getUsers_ShouldReturnAllUsers() throws Exception {
    User user = createUser();
    User user2 = createUser2();
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users")).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[0].id").value(user.getId()))
        .andExpect(jsonPath("$[0].username").value("testUser"))
        .andExpect(jsonPath("$[1].id").value(user2.getId()))
        .andExpect(jsonPath("$[1].username").value("testUser2"));
  }

  @Test
  void getUserByUsername_ShouldReturnUser() throws Exception {
    User user = createUser();
    createUser2();
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/testUser"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.username").value("testUser"));
  }

  @Test
  void getUserByUsername_ShouldReturnNotFound() throws Exception {
    createUser();
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/testUser2"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with username testUser2 not found"));
  }

  @Test
  void addUser_ShouldReturnCreated() throws Exception {
    String json = """
          {
            "username": "NewUser",
            "password": "password",
            "role": "USER"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users").contentType("application/json").content(json))
        .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.username").value("NewUser"));
  }

  @Test
  void deleteUser_ShouldReturnNoContent() throws Exception {
    createUser();
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/testUser"))
        .andExpect(status().isNoContent());
    Assertions.assertEquals(0, userRepository.count());
  }

  @Test
  void deleteUser_ShouldReturnNotFound() throws Exception {
    createUser();
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/testUser2"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with username testUser2 not found"));
    Assertions.assertEquals(1, userRepository.count());
  }

  @Test
  void updateUser_ShouldReturnUpdatedUser() throws Exception {
    User user = createUser();
    String json = """
          {
            "username": "UpdatedUser"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/users/testUser").contentType("application/json")
                .content(json)).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.username").value("UpdatedUser"));
    User updatedUser = userRepository.findById(user.getId()).orElseThrow();
    Assertions.assertEquals("UpdatedUser", updatedUser.getUsername());
  }

  @Test
  void updateUser_ShouldReturnNotFound() throws Exception {
    createUser();
    String json = """
          {
            "username": "UpdatedUser"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/users/testUser2").contentType("application/json")
                .content(json)).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with username testUser2 not found"));
  }

  @Test
  void addUser_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
    String json = """
          {
            "username": " ",
            "password": "password",
            "role": "USER"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users").contentType("application/json").content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Username must not be blank"));
  }

  @Test
  void addUser_ShouldReturnBadRequest_WhenNameIsNull() throws Exception {
    String json = """
          {
            "password": "password",
            "role": "USER"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users").contentType("application/json").content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Username must not be blank"));
  }

  @Test
  @WithMockUser(username = "testUser", roles = "USER")
  void deleteUser_NonAdminUserCanNotDeleteAccountOfOtherUser() throws Exception {
    User user2 = new User();
    user2.setRole(Role.USER);
    user2.setUsername("testUser2");
    user2.setPassword(passwordEncoder.encode("password"));
    user2 = userRepository.save(user2);
    mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/users/" + user2.getId()))
        .andExpect(status().isForbidden());
    boolean exists = userRepository.existsById(user2.getId());
    Assertions.assertTrue(exists);
  }
}
