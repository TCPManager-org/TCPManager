package org.tcpmanager.tcpmanager.user;

import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<@NonNull User, @NonNull Long> {
  Optional<User> findByUsername(String username);

}
