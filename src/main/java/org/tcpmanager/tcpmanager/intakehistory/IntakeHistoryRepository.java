package org.tcpmanager.tcpmanager.intakehistory;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeHistoryRepository extends
    JpaRepository<@NonNull IntakeHistory, @NonNull Long> {

  void deleteIntakeHistoriesByUserUsername(String username);

  List<IntakeHistory> getAllByDate(Date date);

  List<IntakeHistory> getAllByUserUsername(String username);

  Optional<IntakeHistory> findByDate(Date date);

  boolean existsByDate(Date date);

  void deleteByDate(Date date);
}
