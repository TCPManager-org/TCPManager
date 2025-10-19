package org.tcpmanager.tcpmanager.calories.intakehistory;

import jakarta.validation.Valid;
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
import org.tcpmanager.tcpmanager.calories.intakehistory.dto.IntakeHistoryRequest;
import org.tcpmanager.tcpmanager.calories.intakehistory.dto.IntakeHistoryResponse;

@RestController
@RequestMapping("/api/calories/intake-history")
@RequiredArgsConstructor
public class IntakeHistoryController {

  private final IntakeHistoryService intakeHistoryService;

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public IntakeHistoryResponse getIntakeHistoryById(@PathVariable Long id) {
    return intakeHistoryService.getIntakeHistoryById(id);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<IntakeHistoryResponse> getIntakeHistories(){
    return intakeHistoryService.getAllIntakeHistories();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteIntakeHistoryById(@PathVariable Long id) {
    intakeHistoryService.deleteIntakeHistoryById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public IntakeHistoryResponse addIntakeHistory(
      @RequestBody @Valid IntakeHistoryRequest intakeHistoryRequest) {
    return intakeHistoryService.addIntakeHistory(intakeHistoryRequest);
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public IntakeHistoryResponse updateIntakeHistoryById(
      @RequestBody @Valid IntakeHistoryRequest intakeHistoryRequest, @PathVariable Long id) {
    return intakeHistoryService.updateIntakeHistoryById(id, intakeHistoryRequest);
  }
  @DeleteMapping(params = "username")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteHistoryByUsername(@RequestParam String username) {
    intakeHistoryService.deleteHistoryByUsername(username);
  }
}
