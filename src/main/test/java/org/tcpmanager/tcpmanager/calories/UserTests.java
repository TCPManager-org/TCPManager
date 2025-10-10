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
  void invalidUsername_NotUnique() throws Exception {
    User user = new User();
    user.setUsername("TestUser");
    userRepository.save(user);
    UserRequest userRequest = new UserRequest("TestUser");
    mockMvc.perform(MockMvcRequestBuilders.post("/api/users").contentType("application/json")
            .content(asJsonString(userRequest))).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Username TestUser is already taken"));
  }

  @Test
  void invalidUsername_NotAlphanumeric() throws Exception {
    UserRequest userRequest = new UserRequest("Test User!");
    mockMvc.perform(MockMvcRequestBuilders.post("/api/users").contentType("application/json")
            .content(asJsonString(userRequest))).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Username can only contain letters and digits"));
  }

  @Test
  void invalidRequestBody() throws Exception {
    UserRequest userRequest = new UserRequest("");
    mockMvc.perform(MockMvcRequestBuilders.post("/api/users").contentType("application/json")
            .content(asJsonString(userRequest))).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("username must not be blank"));
  }

  @Test
  void getUsers_ShouldReturnAllUsers() throws Exception {
    User user = new User();
    user.setUsername("TestUser");
    user = userRepository.save(user);
    User user2 = new User();
    user2.setUsername("TestUser2");
    user2 = userRepository.save(user2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users")).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[0].id").value(user.getId()))
        .andExpect(jsonPath("$[0].username").value("TestUser"))
        .andExpect(jsonPath("$[1].id").value(user2.getId()))
        .andExpect(jsonPath("$[1].username").value("TestUser2"));
  }

  @Test
  void getUserById_ShouldReturnUser() throws Exception {
    User user = new User();
    user.setUsername("TestUser");
    user = userRepository.save(user);
    User user2 = new User();
    user2.setUsername("TestUser2");
    userRepository.save(user2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + user.getId()))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.username").value("TestUser"));
  }

  @Test
  void getUserById_ShouldReturnNotFound() throws Exception {
    User user = new User();
    user.setUsername("TestUser");
    userRepository.save(user);
    User user2 = new User();
    user2.setUsername("TestUser2");
    userRepository.save(user2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/123")).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with id 123 not found"));
  }

  @Test
  void getUserByUsername_ShouldReturnUser() throws Exception {
    User user = new User();
    user.setUsername("TestUser");
    user = userRepository.save(user);
    User user2 = new User();
    user2.setUsername("TestUser2");
    userRepository.save(user2);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users?username=TestUser"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.username").value("TestUser"));
  }

  @Test
  void getUserByUsername_ShouldReturnNotFound() throws Exception {
    User user = new User();
    user.setUsername("TestUser");
    userRepository.save(user);
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users?username=TestUser2"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with username TestUser2 not found"));
  }

  @Test
  void addUser_ShouldReturnCreated() throws Exception {
    UserRequest userRequest = new UserRequest("NewUser");
    mockMvc.perform(MockMvcRequestBuilders.post("/api/users").contentType("application/json")
            .content(asJsonString(userRequest))).andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber()).andExpect(jsonPath("$.username").value("NewUser"));
  }

  @Test
  void deleteUser_ShouldReturnNoContent() throws Exception {
    User user = new User();
    user.setUsername("TestUser");
    user = userRepository.save(user);
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/" + user.getId()))
        .andExpect(status().isNoContent());
    Assertions.assertEquals(0, userRepository.count());
  }

  @Test
  void deleteUser_ShouldReturnNotFound() throws Exception {
    User user = new User();
    user.setUsername("TestUser");
    userRepository.save(user);
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/123"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with id 123 not found"));
    Assertions.assertEquals(1, userRepository.count());
  }

  @Test
  void updateUser_ShouldReturnUpdatedUser() throws Exception {
    User user = new User();
    user.setUsername("TestUser");
    user = userRepository.save(user);
    UserRequest userRequest = new UserRequest("UpdatedUser");
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/users/" + user.getId()).contentType("application/json")
                .content(asJsonString(userRequest))).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.username").value("UpdatedUser"));
    User updatedUser = userRepository.findById(user.getId()).orElseThrow();
    Assertions.assertEquals("UpdatedUser", updatedUser.getUsername());
  }

  @Test
  void updateUser_ShouldReturnNotFound() throws Exception {
    User user = new User();
    user.setUsername("TestUser");
    userRepository.save(user);
    UserRequest userRequest = new UserRequest("UpdatedUser");
    mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/123").contentType("application/json")
            .content(asJsonString(userRequest))).andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with id 123 not found"));
  }

  @Test
  void addUser_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
    UserRequest userRequest = new UserRequest(" ");
    mockMvc.perform(MockMvcRequestBuilders.post("/api/users").contentType("application/json")
            .content(asJsonString(userRequest))).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("username must not be blank"));
  }

  @Test
  void addUser_ShouldReturnBadRequest_WhenNameIsNull() throws Exception {
    UserRequest userRequest = new UserRequest(null);
    mockMvc.perform(MockMvcRequestBuilders.post("/api/users").contentType("application/json")
            .content(asJsonString(userRequest))).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("username must not be blank"));
  }

  private String asJsonString(Object object) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(object);
  }
}
