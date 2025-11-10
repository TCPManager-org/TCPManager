package org.tcpmanager.tcpmanager.calories.day;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.tcpmanager.tcpmanager.calories.day.dto.DayRequest;
import org.tcpmanager.tcpmanager.calories.day.dto.DayResponse;

@RestController
@RequestMapping("/api/calories/days")
@RequiredArgsConstructor
public class DayController {

  private final DayService dayService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<DayResponse> getAllDays() {
    return dayService.getAllDays();
  }
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DayResponse addDay(@RequestBody @Valid DayRequest dayRequest) {
    return dayService.addDay(dayRequest);
  }

}
