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
    User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    return new UserResponse(user.getId(), user.getUsername());
  }

  @Transactional
  public void deleteById(Long id) {
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException(id);
    }

    userRepository.deleteById(id);
  }

  @Transactional
  public UserResponse updateById(Long id, UserRequest userRequest) {
    User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    var username = userRequest.username().strip();
    validateUsername(username);
    user.setUsername(username);
    userRepository.save(user);
    return new UserResponse(user.getId(), user.getUsername());
  }

  @Transactional
  public UserResponse add(UserRequest userRequest) {
    User user = new User();
    var username = userRequest.username().strip();
    validateUsername(username);
    user.setUsername(username);
    user = userRepository.save(user);
    return new UserResponse(user.getId(), user.getUsername());
  }

  public UserResponse getByUsername(String username) {
    return userRepository.findByUsername(username)
        .map(user -> new UserResponse(user.getId(), user.getUsername()))
        .orElseThrow(() -> new UserNotFoundException(username));
  }
  private void validateUsername(String username) {
    if (userRepository.existsByUsername(username)) {
      throw new IllegalUsernameException("Username " + username + " is already taken");
    }
    if(!username.chars().allMatch(Character::isLetterOrDigit)) {
      throw new IllegalUsernameException("Username can only contain letters and digits");
    }
  }
}
