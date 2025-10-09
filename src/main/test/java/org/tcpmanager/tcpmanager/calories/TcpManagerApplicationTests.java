package org.tcpmanager.tcpmanager.calories;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.test.context.TestPropertySource;
import org.tcpmanager.tcpmanager.TcpManagerApplication;

@SpringBootTest
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver", "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=none"})
class TcpManagerApplicationTests {

  static ApplicationModules applicationModules = ApplicationModules.of(TcpManagerApplication.class);

  @Test
  void contextLoads() {
    applicationModules.verify();
  }
}
