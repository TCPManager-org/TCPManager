package org.tcpmanager.tcpmanager.calories.day;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.day.dto.DayResponse;
import org.tcpmanager.tcpmanager.calories.day.models.Day;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DayService {

  private final DayRepository dayRepository;

  private static DayResponse mapToDayResponse(Day day) {
    Map<Long, Integer> portionsByMealId = new HashMap<>();
    day.getDayMeals()
        .forEach(dayMeal -> portionsByMealId.put(dayMeal.getMeal().getId(), dayMeal.getWeight()));
    return new DayResponse(day.getDate(), portionsByMealId);
  }

  public List<DayResponse> getAllDays() {
    return dayRepository.findAll().stream()
        .map(DayService::mapToDayResponse)
        .toList();
  }
}
