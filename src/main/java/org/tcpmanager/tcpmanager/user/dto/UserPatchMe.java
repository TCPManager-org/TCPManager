package org.tcpmanager.tcpmanager.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.tcpmanager.tcpmanager.user.Role;

public record UserPatchMe(@Size(min = 3) String username, @Schema(hidden = true) Role role) {

}
