package org.tcpmanager.tcpmanager.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserPatch(@NotBlank String username) {

}
