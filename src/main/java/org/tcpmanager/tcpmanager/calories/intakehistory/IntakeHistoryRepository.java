package org.tcpmanager.tcpmanager.calories.intakehistory;

import java.sql.Date;
import java.util.List;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeHistoryRepository extends
    JpaRepository<@NonNull IntakeHistory, @NonNull Long> {

  void deleteIntakeHistoriesByUsername(String username);

  List<IntakeHistory> getAllByUsername(String username);

  List<IntakeHistory> getAllByDate(Date date);
}
