package org.tcpmanager.tcpmanager.calories.day;

import java.sql.Date;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.tcpmanager.tcpmanager.calories.day.models.Day;

public interface DayRepository extends JpaRepository<@NonNull Day, @NonNull Long> {

  boolean existsByDate(Date date);

  void deleteByDate(Date date);

  Optional<Day> findByDateAndUserUsername(Date date, String username);

  Optional<Day> findByDayMealsId(Long dayMealId);

  Optional<Day> findByDateAndDayMealsId(Date date, Long dayMealId);
}
