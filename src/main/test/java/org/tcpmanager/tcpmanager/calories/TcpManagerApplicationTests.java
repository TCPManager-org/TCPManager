package org.tcpmanager.tcpmanager.calories;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.tcpmanager.tcpmanager.TcpManagerApplication;

@SpringBootTest
class TcpManagerApplicationTests {

  static ApplicationModules applicationModules = ApplicationModules.of(TcpManagerApplication.class);

  @Test
  void contextLoads() {
    applicationModules.verify();
  }
}
