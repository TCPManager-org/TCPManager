package org.tcpmanager.tcpmanager.intakehistory;

import java.sql.Date;
import java.util.Set;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeHistoryRepository extends
    JpaRepository<@NonNull IntakeHistory, @NonNull Long> {

  void deleteIntakeHistoriesByUserUsername(String username);

  Set<IntakeHistory> getAllByDate(Date date);

  Set<IntakeHistory> getAllByUserUsername(String username);

  Set<IntakeHistory> getByDate(Date date);
}
