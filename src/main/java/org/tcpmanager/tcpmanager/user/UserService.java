package org.tcpmanager.tcpmanager.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.user.dto.UserRequest;
import org.tcpmanager.tcpmanager.user.dto.UserResponse;
import org.tcpmanager.tcpmanager.user.exception.IllegalUsernameException;
import org.tcpmanager.tcpmanager.user.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;

  public List<UserResponse> getAll() {
    return userRepository.findAll().stream()
        .map(user -> new UserResponse(user.getId(), user.getUsername())).toList();
  }

  public UserResponse getById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    return new UserResponse(user.getId(), user.getUsername());
  }

  @Transactional
  public void deleteById(Long id) {
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException("User with id " + id + " not found");
    }

    userRepository.deleteById(id);
  }

  @Transactional
  public UserResponse updateById(Long id, UserRequest userRequest) {
    validateUserRequest(userRequest);
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    user.setUsername(userRequest.username());
    userRepository.save(user);
    return new UserResponse(user.getId(), user.getUsername());
  }

  @Transactional
  public UserResponse add(UserRequest userRequest) {
    validateUserRequest(userRequest);
    User user = new User();
    user.setUsername(userRequest.username());
    user = userRepository.save(user);
    return new UserResponse(user.getId(), user.getUsername());
  }

  private void validateUserRequest(UserRequest userRequest) {
    if (userRequest.username() == null || userRequest.username().isBlank()) {
      throw new IllegalUsernameException("User name cannot be null or blank");
    }
  }
}
