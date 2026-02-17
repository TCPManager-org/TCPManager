package org.tcpmanager.tcpmanager.user.dto;

import jakarta.validation.constraints.Size;
import org.tcpmanager.tcpmanager.user.Role;

public record UserPatch(@Size(min = 3) String username, Role role) {

}
