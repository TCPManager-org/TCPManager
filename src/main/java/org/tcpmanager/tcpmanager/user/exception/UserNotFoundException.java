package org.tcpmanager.tcpmanager.user.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String username) {
    super("User with name " + username + " not found");
  }
  public UserNotFoundException(Long id) {
    super("User with id " + id + " not found");
  }
}
