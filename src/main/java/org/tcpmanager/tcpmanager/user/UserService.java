package org.tcpmanager.tcpmanager.user;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.user.dto.UserRequest;
import org.tcpmanager.tcpmanager.user.dto.UserResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;

  private static String generateNotFoundMessage(Long id) {
    return "User with id " + id + " not found";
  }

  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream()
        .map(user -> new UserResponse(user.getId(), user.getUsername())).toList();
  }

  public UserResponse getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    return new UserResponse(user.getId(), user.getUsername());
  }

  @Transactional
  public void deleteUserById(Long id) {
    if (!userRepository.existsById(id)) {
      throw new EntityNotFoundException(generateNotFoundMessage(id));
    }
    userRepository.deleteById(id);
  }

  @Transactional
  public UserResponse updateUserById(Long id, UserRequest userRequest) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    var username = userRequest.username().strip();
    validateUsername(username);
    user.setUsername(username);
    userRepository.save(user);
    return new UserResponse(user.getId(), user.getUsername());
  }

  @Transactional
  public UserResponse addUser(UserRequest userRequest) {
    User user = new User();
    var username = userRequest.username().strip();
    validateUsername(username);
    user.setUsername(username);
    user = userRepository.save(user);
    return new UserResponse(user.getId(), user.getUsername());
  }

  public UserResponse getUserByUsername(String username) {
    return userRepository.findByUsername(username)
        .map(user -> new UserResponse(user.getId(), user.getUsername())).orElseThrow(
            () -> new EntityNotFoundException("User with username " + username + " not found"));
  }

  private void validateUsername(String username) {
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("Username " + username + " is already taken");
    }
    if (!username.chars().allMatch(Character::isLetterOrDigit)) {
      throw new IllegalArgumentException("Username can only contain letters and digits");
    }
  }
}
