package org.tcpmanager.tcpmanager.calories.intakehistory;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeHistoryRepository extends
    JpaRepository<@NonNull IntakeHistory, @NonNull Long> {

}
