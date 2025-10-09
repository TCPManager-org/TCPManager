package org.tcpmanager.tcpmanager;

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
import org.tcpmanager.tcpmanager.user.User;
import org.tcpmanager.tcpmanager.user.UserRepository;
import org.tcpmanager.tcpmanager.user.dto.UserRequest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
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

  @AfterEach
  void afterEach() {
    userRepository.deleteAll();
  }

  @Test
  void getUsers_ShouldReturnAllUsers() throws Exception {
    User user = new User();
    user.setUsername("Test User");
    user = userRepository.save(user);
    User user2 = new User();
    user2.setUsername("Test User2");
    user2 = userRepository.save(user2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users")).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[0].id").value(user.getId()))
        .andExpect(jsonPath("$[0].username").value("Test User"))
        .andExpect(jsonPath("$[1].id").value(user2.getId()))
        .andExpect(jsonPath("$[1].username").value("Test User2"));
  }

  @Test
  void getUser_ShouldReturnNotFound() throws Exception {
    User user = new User();
    user.setUsername("Test User");
    userRepository.save(user);
    User user2 = new User();
    user2.setUsername("Test User2");
    userRepository.save(user2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/123"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with id 123 not found"));
  }

  @Test
  void getUser_ShouldReturnUser() throws Exception {
    User user = new User();
    user.setUsername("Test User");
    user = userRepository.save(user);
    User user2 = new User();
    user2.setUsername("Test User2");
    userRepository.save(user2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users?username=Test User"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.username").value("Test User"));
  }

  @Test
  void addUser_ShouldReturnCreated() throws Exception {
    UserRequest userRequest = new UserRequest("New User");
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users").contentType("application/json")
                .content(asJsonString(userRequest))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber()).andExpect(jsonPath("$.username").value("New User"));
  }

  @Test
  void deleteUser_ShouldReturnNoContent() throws Exception {
    User user = new User();
    user.setUsername("Test User");
    user = userRepository.save(user);
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/" + user.getId()))
        .andExpect(status().isNoContent());
    Assertions.assertEquals(0, userRepository.count());
  }

  @Test
  void deleteUser_ShouldReturnNotFound() throws Exception {
    User user = new User();
    user.setUsername("Test User");
    userRepository.save(user);
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/123"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with id 123 not found"));
    Assertions.assertEquals(1, userRepository.count());
  }

  @Test
  void updateUser_ShouldReturnUpdatedUser() throws Exception {
    User user = new User();
    user.setUsername("Test User");
    user = userRepository.save(user);
    UserRequest userRequest = new UserRequest("Updated User");
    mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/" + user.getId())
            .contentType("application/json").content(asJsonString(userRequest)))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.username").value("Updated User"));
    User updatedUser = userRepository.findById(user.getId()).orElseThrow();
    Assertions.assertEquals("Updated User", updatedUser.getUsername());
  }

  @Test
  void updateUser_ShouldReturnNotFound() throws Exception {
    User user = new User();
    user.setUsername("Test User");
    userRepository.save(user);
    UserRequest userRequest = new UserRequest("Updated User");
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/users/123").contentType("application/json")
                .content(asJsonString(userRequest))).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with id 123 not found"));
  }

  @Test
  void addUser_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
    UserRequest userRequest = new UserRequest(" ");
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users").contentType("application/json")
                .content(asJsonString(userRequest))).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("username must not be blank"));
  }

  @Test
  void addUser_ShouldReturnBadRequest_WhenNameIsNull() throws Exception {
    UserRequest userRequest = new UserRequest(null);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users").contentType("application/json")
                .content(asJsonString(userRequest))).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("username must not be blank"));
  }

  private String asJsonString(Object object) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(object);
  }
}
