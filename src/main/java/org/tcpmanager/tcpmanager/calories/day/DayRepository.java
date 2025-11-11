package org.tcpmanager.tcpmanager.calories.day;

import jakarta.validation.constraints.NotNull;
import java.sql.Date;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.tcpmanager.tcpmanager.calories.day.models.Day;

public interface DayRepository extends JpaRepository<@NonNull Day, @NonNull Long> {

  boolean existsByDateAndUserId(@NotNull Date date, @NotNull Long userId);

  boolean existsByDate(Date date);

  void deleteByDate(Date date);

  Optional<Day> findByDateAndUserId(Date date, Long userId);
}
