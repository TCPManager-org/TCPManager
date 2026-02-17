package org.tcpmanager.tcpmanager.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.security.Principal;
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

  @GetMapping(value = "/{username}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse getUserByUsername(@NotBlank @PathVariable String username) {
    return userService.getUserByUsername(username);
  }

  @GetMapping(value = "/me", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse getMyUser(Principal principal) {
    return userService.getUserByUsername(principal.getName());
  }

  @PostMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse addUser(@RequestBody @Valid UserRequest userRequest) {
    return userService.addUser(userRequest);
  }

  @PatchMapping(value = "/{username}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse updateUserByUsername(@PathVariable String username,
      @RequestBody @Valid UserPatch userPatch) {
    return userService.updateUserByUsername(username, userPatch, false);
  }

  @PatchMapping(value = "/me", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse updateMyUser(@RequestBody @Valid UserPatch userPatch, Principal principal) {
    return userService.updateUserByUsername(principal.getName(), userPatch, true);
  }

  @DeleteMapping("/{username}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUserByUsername(@PathVariable String username) {
    userService.deleteUserByUsername(username);
  }

  @DeleteMapping("/me")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteMyUser(Principal principal) {
    userService.deleteUserByUsername(principal.getName());
  }
}
