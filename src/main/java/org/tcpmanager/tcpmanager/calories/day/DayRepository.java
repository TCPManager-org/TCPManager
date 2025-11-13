package org.tcpmanager.tcpmanager.calories.day;

import java.sql.Date;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.tcpmanager.tcpmanager.calories.day.models.Day;

public interface DayRepository extends JpaRepository<@NonNull Day, @NonNull Long> {

  Optional<Day> findByDateAndUserUsername(Date date, String username);

  Set<Day> findByDate(Date date);
}
