package org.tcpmanager.tcpmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.tcpmanager.tcpmanager.security.RsaKeyProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class TcpManagerApplication {

  public static void main(String[] args) {
    SpringApplication.run(TcpManagerApplication.class, args);
  }
}