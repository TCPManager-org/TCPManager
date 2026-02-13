package org.tcpmanager.tcpmanager.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.tcpmanager.tcpmanager.user.dto.UserPatch;
import org.tcpmanager.tcpmanager.user.dto.UserRequest;
import org.tcpmanager.tcpmanager.user.dto.UserResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public List<UserResponse> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping(value = "/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse getUserById(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  @GetMapping(params = {"username"}, produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse getUserByUsername(
      @NotBlank @RequestParam(value = "username") String username) {
    return userService.getUserByUsername(username);
  }

  @PostMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse addUser(@RequestBody @Valid UserRequest userRequest) {
    return userService.addUser(userRequest);
  }

  @PatchMapping(value = "/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse updateUserById(@PathVariable Long id,
      @RequestBody @Valid UserPatch userPatch) {
    return userService.updateUserById(id, userPatch);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUserById(@PathVariable Long id) {
    userService.deleteUserById(id);
  }
}
