package org.tcpmanager.tcpmanager.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(@NotBlank @Size(min = 3) String username, @NotBlank String password,
                          @NotBlank String role) {

}
