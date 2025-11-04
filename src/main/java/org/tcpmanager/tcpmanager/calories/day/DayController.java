package org.tcpmanager.tcpmanager.calories.day;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calories/days")
@RequiredArgsConstructor
public class DayController {

  private final DayService dayService;

}
