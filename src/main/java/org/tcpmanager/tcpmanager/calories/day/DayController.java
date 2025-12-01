package org.tcpmanager.tcpmanager.calories.day;

import jakarta.validation.Valid;
import java.sql.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.tcpmanager.tcpmanager.calories.day.dto.DayMealPatch;
import org.tcpmanager.tcpmanager.calories.day.dto.DayMealRequest;
import org.tcpmanager.tcpmanager.calories.day.dto.DayResponse;

@RestController
@RequestMapping("/api/calories/days")
@RequiredArgsConstructor
public class DayController {

  private final DayService dayService;

  @GetMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public List<DayResponse> getAllDays() {
    return dayService.getAllDays();
  }

  @PostMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public DayResponse addMealToDay(@RequestBody @Valid DayMealRequest dayMealRequest) {
    return dayService.addMealToDay(dayMealRequest);
  }

  @DeleteMapping("/{date}/{dayMealId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteMealFromDay(@PathVariable Date date, @PathVariable Long dayMealId,
      @RequestParam String username) {
    dayService.deleteMealFromDay(date, dayMealId, username);
  }

  @PatchMapping("/{date}/{dayMealId}")
  @ResponseStatus(HttpStatus.OK)
  public DayResponse updateMealFromDay(@PathVariable Date date, @PathVariable Long dayMealId,
      @RequestParam String username, @RequestBody @Valid DayMealPatch dayMealPatch) {
    return dayService.updateMealFromDay(date, dayMealId, username, dayMealPatch);
  }

  @DeleteMapping("/{date}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDayByDate(@RequestParam String username, @PathVariable Date date) {
    dayService.deleteByDate(username, date);
  }

}
