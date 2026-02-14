package org.tcpmanager.tcpmanager.user;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.user.dto.UserPatch;
import org.tcpmanager.tcpmanager.user.dto.UserRequest;
import org.tcpmanager.tcpmanager.user.dto.UserResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  public static String generateNotFoundMessage(String username) {
    return "User with username " + username + " not found";
  }

  private static UserResponse mapToUserResponse(User user) {
    return new UserResponse(user.getId(), user.getUsername(), String.valueOf(user.getRole()));
  }

  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream().map(UserService::mapToUserResponse).toList();
  }

  public UserResponse getUserByUsername(String username) {
    return userRepository.findByUsername(username).map(UserService::mapToUserResponse)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(username)));
  }

  @Transactional
  public UserResponse addUser(UserRequest userRequest) {
    User user = new User();
    var username = userRequest.username().strip();
    validateUsername(username);
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(userRequest.password()));
    user.setRole(Role.valueOf(userRequest.role()));
    user = userRepository.save(user);
    return mapToUserResponse(user);
  }

  @Transactional
  public UserResponse updateUserByUsername(String username, UserPatch userPatch) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(username)));
    if (userPatch.username() != null) {
      String newUsername = userPatch.username().strip();
      validateUsername(newUsername);
      user.setUsername(newUsername);
    }
    if (userPatch.role() != null) {
      user.setRole(Role.valueOf(userPatch.role()));
    }
    userRepository.save(user);
    return mapToUserResponse(user);
  }

  @Transactional
  public void deleteUserByUsername(String username) {
    if (!userRepository.existsByUsername(username)) {
      throw new EntityNotFoundException(generateNotFoundMessage(username));
    }
    userRepository.deleteByUsername(username);
  }

  private void validateUsername(String username) {
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("Username " + username + " is already taken");
    }
    if (!username.chars().allMatch(Character::isLetterOrDigit)) {
      throw new IllegalArgumentException("Username can only contain letters and digits");
    }
  }

  @Override
  @NullMarked
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(generateNotFoundMessage(username)));
    return org.springframework.security.core.userdetails.User.builder().username(user.getUsername())
        .password(user.getPassword()).roles(String.valueOf(user.getRole())).build();
  }
}
