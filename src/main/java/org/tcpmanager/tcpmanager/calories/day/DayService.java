package org.tcpmanager.tcpmanager.calories.day;

import java.util.List;
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

  }

  public List<DayResponse> getAllDays() {

  }
}
