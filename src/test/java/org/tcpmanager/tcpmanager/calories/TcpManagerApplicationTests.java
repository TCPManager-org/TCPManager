package org.tcpmanager.tcpmanager.calories;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.security.test.context.support.WithMockUser;
import org.tcpmanager.tcpmanager.TcpManagerApplication;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@WithMockUser(username = "testUser", roles = "ADMIN")
class TcpManagerApplicationTests {

  static ApplicationModules applicationModules = ApplicationModules.of(TcpManagerApplication.class);

  @SuppressWarnings("resource")
  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
      "postgres:17").withDatabaseName("tcp").withUsername("root").withPassword("root");

  static {
    postgreSQLContainer.start();
  }

  @Test
  void contextLoads() {
    applicationModules.verify();
  }
}
