package org.tcpmanager.tcpmanager.intakehistory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import org.tcpmanager.tcpmanager.intakehistory.dto.IntakeHistoryPatch;
import org.tcpmanager.tcpmanager.intakehistory.dto.IntakeHistoryRequest;
import org.tcpmanager.tcpmanager.intakehistory.dto.IntakeHistoryResponse;

@RestController
@RequestMapping("/api/intake-history")
@RequiredArgsConstructor
public class IntakeHistoryController {

  private final IntakeHistoryService intakeHistoryService;

  @GetMapping("/{date}")
  @ResponseStatus(HttpStatus.OK)
  public IntakeHistoryResponse getIntakeHistoryById(@PathVariable Date date) {
    return intakeHistoryService.getIntakeHistoryById(date);
  }

  @GetMapping(params = "username")
  @ResponseStatus(HttpStatus.OK)
  public List<IntakeHistoryResponse> getIntakeHistoriesByUsername(
      @NotBlank @RequestParam String username) {
    return intakeHistoryService.getAllIntakeHistoriesByUsername(username);
  }

  @DeleteMapping("/{date}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteIntakeHistoryById(@PathVariable Date date) {
    intakeHistoryService.deleteIntakeHistoryById(date);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public IntakeHistoryResponse addIntakeHistory(
      @RequestBody @Valid IntakeHistoryRequest intakeHistoryRequest) {
    return intakeHistoryService.addIntakeHistory(intakeHistoryRequest);
  }

  @PatchMapping("/{date}")
  @ResponseStatus(HttpStatus.OK)
  public IntakeHistoryResponse updateIntakeHistoryById(
      @RequestBody @Valid IntakeHistoryPatch intakeHistoryPatch, @PathVariable Date date) {
    return intakeHistoryService.updateIntakeHistoryById(date, intakeHistoryPatch);
  }

  @DeleteMapping(params = "username")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteHistoryByUsername(@RequestParam String username) {
    intakeHistoryService.deleteHistoryByUsername(username);
  }
}
