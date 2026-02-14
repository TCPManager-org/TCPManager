package org.tcpmanager.tcpmanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class TokenController {

  private final TokenService tokenService;

  @PostMapping(produces = "application/json")
  public String token(Authentication authentication) {
    return tokenService.generateToken(authentication);
  }
}