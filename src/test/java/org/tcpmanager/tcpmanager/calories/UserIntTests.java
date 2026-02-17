package org.tcpmanager.tcpmanager.calories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.user.Role;
import org.tcpmanager.tcpmanager.user.User;
import org.tcpmanager.tcpmanager.user.UserRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserIntTests {

  private static final String ADMIN_USERNAME = "testAdmin";
  private static final String USER_USERNAME = "testUser";

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

  private static Stream<Arguments> userProvider() {
    return Stream.of(
        Arguments.of("testUser", "USER"),
        Arguments.of("testAdmin", "ADMIN")
    );
  }

  private static Stream<Arguments> userProviderWithChangedRole() {
    return Stream.of(
        Arguments.of("testUser", "USER", "ADMIN"),
        Arguments.of("testAdmin", "ADMIN", "USER")
    );
  }

  @BeforeEach
  @Transactional
  void beforeEach() {
    userRepository.deleteAll();

    var admin = new User();
    admin.setUsername("testAdmin");
    admin.setPassword(passwordEncoder.encode("admin"));
    admin.setRole(Role.ADMIN);
    userRepository.save(admin);

    var user = new User();
    user.setUsername("testUser");
    user.setPassword(passwordEncoder.encode("user"));
    user.setRole(Role.USER);
    userRepository.save(user);

    userRepository.deleteByUsername("admin");
  }

  @ParameterizedTest
  @ValueSource(strings = {"/api/users/testUser", "/api/users/testAdmin"})
  void user_cantUseAdminEndpoints(String uri) throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get(uri).with(user(USER_USERNAME).roles("USER")))
        .andExpect(status().isForbidden());
    mockMvc.perform(MockMvcRequestBuilders.delete(uri).with(user(USER_USERNAME).roles("USER")))
        .andExpect(status().isForbidden());
    final String json = """
          {
            "username": "test",
            "role": "USER"
          }
        """;
    mockMvc.perform(MockMvcRequestBuilders.post(uri).contentType("application/json").content(json)
            .with(user(USER_USERNAME).roles("USER")))
        .andExpect(status().isForbidden());

    mockMvc.perform(MockMvcRequestBuilders.patch(uri).contentType("application/json").content(json)
            .with(user(USER_USERNAME).roles("USER")))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("userProvider")
  void getMyUser(String username, String role) throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me")
            .with(user(username).roles(role)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value(username))
        .andExpect(jsonPath("$.role").value(role));
  }

  @Test
  void getOtherUser_admin() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + USER_USERNAME)
            .with(user(ADMIN_USERNAME).roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("testUser"))
        .andExpect(jsonPath("$.role").value("USER"));
  }

  @Test
  void getOtherUser_admin_doesNotExist() throws Exception {
    userRepository.deleteByUsername("nonExisting");
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/nonExisting")
            .with(user(ADMIN_USERNAME).roles("ADMIN")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with username nonExisting not found"));
  }

  @ParameterizedTest
  @MethodSource("userProvider")
  void updateMyUser(String username, Role role) throws Exception {
    final String json = """
          {
            "username": "updatedUsername"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/users/me")
                .contentType("application/json").content(json).with(user(username).roles(
                    String.valueOf(role))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("updatedUsername"));
    var user = userRepository.findByUsername("updatedUsername");
    assertTrue(user.isPresent());
    assertEquals(role, user.get().getRole());
  }

  @ParameterizedTest
  @MethodSource("userProviderWithChangedRole")
  void updateMyUser_noUpdatingRole(String username, Role role, Role changedRole)
      throws Exception {
    final String json = """
          {
            "role": "%s"
          }
        """.formatted(changedRole);
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/users/me").with(user(username))
                .contentType("application/json").content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("You cannot change your own role"));
    var user = userRepository.findByUsername(username);
    assertTrue(user.isPresent());
    assertEquals(role, user.get().getRole());
  }

  @ParameterizedTest
  @MethodSource("userProvider")
  void updateMyUser_invalidUsername(String username, Role role) throws Exception {
    final String json = """
          {
            "username": "%!@#@!"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/users/me")
                .contentType("application/json").content(json).with(user(username).roles(
                    String.valueOf(role))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(
            "Username can only contain letters, digits, underscores and hyphens"));
    var user = userRepository.findByUsername(username);
    assertTrue(user.isPresent());
    assertEquals(role, user.get().getRole());
  }

  @ParameterizedTest
  @MethodSource("userProvider")
  void updateMyUser_meIsForbidden(String username, Role role) throws Exception {
    final String json = """
          {
            "username": "me"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/users/me")
                .contentType("application/json").content(json).with(user(username).roles(
                    String.valueOf(role))))
        .andExpect(status().isBadRequest());
    var user = userRepository.findByUsername(username);
    assertTrue(user.isPresent());
    assertEquals(role, user.get().getRole());
  }

  @ParameterizedTest
  @MethodSource("userProvider")
  void updateMyUser_usernameTaken(String username, Role role) throws Exception {
    User existingUser = new User();
    existingUser.setUsername("existing");
    existingUser.setPassword(passwordEncoder.encode("password"));
    existingUser.setRole(role);
    userRepository.save(existingUser);

    final String json = """
          {
            "username": "existing"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/users/me")
                .contentType("application/json").content(json).with(user(username).roles(
                    String.valueOf(role))))
        .andExpect(status().isBadRequest());
    var user = userRepository.findByUsername(username);
    assertTrue(user.isPresent());
    assertEquals(role, user.get().getRole());
  }

  @ParameterizedTest
  @ValueSource(strings = {USER_USERNAME, ADMIN_USERNAME})
  void deleteMyUser(String username) throws Exception {
    var user = userRepository.findByUsername(username);
    assertTrue(user.isPresent());
    if (user.get().getRole().equals(Role.ADMIN)) {
      User admin = new User();
      admin.setUsername("admin");
      admin.setPassword(passwordEncoder.encode("admin"));
      admin.setRole(Role.ADMIN);
      userRepository.save(admin);
    }
    mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/users/me").with(user(username)))
        .andExpect(status().isNoContent());
    assertTrue(userRepository.findByUsername(username).isEmpty());
  }

  @Test
  void deleteMyUser_admin_noDeletingOnlyAdmin() throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/users/me").with(user(ADMIN_USERNAME).roles("ADMIN")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Cannot delete the only admin"));
    assertTrue(userRepository.findByUsername("testAdmin").isPresent());
  }

  @Test
  void deleteOtherUser_admin() throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/users/" + USER_USERNAME)
                .with(user(ADMIN_USERNAME).roles("ADMIN")))
        .andExpect(status().isNoContent());
    assertTrue(userRepository.findByUsername(USER_USERNAME).isEmpty());
  }

  @Test
  void deleteOtherUser_admin_doesNotExist() throws Exception {
    userRepository.deleteByUsername("nonExisting");
    mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/nonExisting")
            .with(user(ADMIN_USERNAME).roles("ADMIN")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with username nonExisting not found"));
  }

  @Test
  void updateOtherUser_admin() throws Exception {
    final String json = """
          {
            "username": "updatedUsername",
            "role": "ADMIN"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/users/" + USER_USERNAME)
                .contentType("application/json").content(json)
                .with(user(ADMIN_USERNAME).roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("updatedUsername"))
        .andExpect(jsonPath("$.role").value("ADMIN"));
    var user = userRepository.findByUsername("updatedUsername");
    assertTrue(user.isPresent());
    assertEquals(Role.ADMIN, user.get().getRole());
  }

  @Test
  void updateOtherUser_admin_doesNotExist() throws Exception {
    userRepository.deleteByUsername("nonExisting");
    final String json = """
          {
            "username": "updatedUsername",
            "role": "ADMIN"
          }
        """;
    mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/users/nonExisting")
                .contentType("application/json").content(json)
                .with(user(ADMIN_USERNAME).roles("ADMIN")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with username nonExisting not found"));
  }

  @Test
  void addUser_admin() throws Exception {
    final String json = """
          {
            "username": "newUser",
            "password": "password",
            "role": "USER"
          }
        """;
    mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
            .contentType("application/json").content(json).with(user(ADMIN_USERNAME).roles("ADMIN")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("newUser"))
        .andExpect(jsonPath("$.role").value("USER"));
    var user = userRepository.findByUsername("newUser");
    assertTrue(user.isPresent());
    assertEquals(Role.USER, user.get().getRole());
  }
  @Test
  void getAllUsers_admin() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
            .with(user(ADMIN_USERNAME).roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }
}
