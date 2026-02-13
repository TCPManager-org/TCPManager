package org.tcpmanager.tcpmanager.user.dto;

import jakarta.validation.constraints.Size;

public record UserPatch(@Size(min = 3) String username, String role) {

}
