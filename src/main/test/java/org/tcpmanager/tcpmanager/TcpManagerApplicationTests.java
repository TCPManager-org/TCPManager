package org.tcpmanager.tcpmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class TcpManagerApplicationTests {

  static ApplicationModules applicationModules = ApplicationModules.of(TcpManagerApplication.class);

  @Test
  void contextLoads() {
    applicationModules.verify();
  }
}
