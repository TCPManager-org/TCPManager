package org.tcpmanager.tcpmanager.user;

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
import org.tcpmanager.tcpmanager.user.dto.UserRequest;
import org.tcpmanager.tcpmanager.user.dto.UserResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<UserResponse> getMeals() {
    return userService.getAll();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse getMealById(@PathVariable Long id) {
    return userService.getById(id);
  }

  @GetMapping("/{username}")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse getMealById(@PathVariable String username) {
    return userService.getByUsername(username);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteMealById(@PathVariable Long id) {
    userService.deleteById(id);
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse updateMealById(@PathVariable Long id, @RequestBody UserRequest userRequest) {
    return userService.updateById(id, userRequest);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse addMeal(@RequestBody UserRequest userRequest) {
    return userService.add(userRequest);
  }
}
