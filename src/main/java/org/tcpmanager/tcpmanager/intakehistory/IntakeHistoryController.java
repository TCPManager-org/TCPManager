package org.tcpmanager.tcpmanager.intakehistory;

import jakarta.validation.Valid;
import java.security.Principal;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.tcpmanager.tcpmanager.intakehistory.dto.IntakeHistoryPatch;
import org.tcpmanager.tcpmanager.intakehistory.dto.IntakeHistoryRequest;
import org.tcpmanager.tcpmanager.intakehistory.dto.IntakeHistoryResponse;

@RestController
@RequestMapping("/api/statistics/intake-history")
@RequiredArgsConstructor
public class IntakeHistoryController {

  private final IntakeHistoryService intakeHistoryService;

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public IntakeHistoryResponse getIntakeHistoryById(@PathVariable Long id, Principal principal) {
    return intakeHistoryService.getIntakeHistoryById(id, principal.getName());
  }
  @GetMapping()
  @ResponseStatus(HttpStatus.OK)
  public List<IntakeHistoryResponse> getAllIntakeHistories(Principal principal) {
    return intakeHistoryService.getAllIntakeHistories(principal.getName());
  }
  @GetMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public List<IntakeHistoryResponse> getIntakeHistoriesByUsername(
      Principal principal) {
    return intakeHistoryService.getAllIntakeHistoriesByUsername(principal.getName());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteIntakeHistoryById(@PathVariable Long id) {
    intakeHistoryService.deleteIntakeHistoryById(id);
  }

  @PostMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public IntakeHistoryResponse addIntakeHistory(
      @RequestBody @Valid IntakeHistoryRequest intakeHistoryRequest, Principal principal) {
    return intakeHistoryService.addIntakeHistory(intakeHistoryRequest, principal.getName());
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public IntakeHistoryResponse updateIntakeHistoryById(
      @RequestBody @Valid IntakeHistoryPatch intakeHistoryPatch, @PathVariable Long id) {
    return intakeHistoryService.updateIntakeHistoryById(id, intakeHistoryPatch);
  }

  @DeleteMapping()
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteHistoryByUsername(Principal principal) {
    intakeHistoryService.deleteHistoryByUsername(principal.getName());
  }
}
