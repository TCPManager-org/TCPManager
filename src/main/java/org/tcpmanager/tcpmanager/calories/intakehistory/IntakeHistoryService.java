package org.tcpmanager.tcpmanager.calories.intakehistory;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.intakehistory.dto.IntakeHistoryRequest;
import org.tcpmanager.tcpmanager.calories.intakehistory.dto.IntakeHistoryResponse;
import org.tcpmanager.tcpmanager.events.UserDeletedEvent;
import org.tcpmanager.tcpmanager.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntakeHistoryService {

  private final IntakeHistoryRepository intakeHistoryRepository;
  private final UserService userService;

  private static String generateNotFoundMessage(Long id) {
    return "Intake history with id " + id + " not found";
  }

  @ApplicationModuleListener
  @Transactional
  public void on(UserDeletedEvent event) {
    intakeHistoryRepository.deleteIntakeHistoriesByUsername(event.username());
  }

  private IntakeHistoryResponse mapToIntakeHistoryResponse(IntakeHistory intakeHistory) {
    return new IntakeHistoryResponse(intakeHistory.getId(), intakeHistory.getDate(),
        intakeHistory.getCalories(), intakeHistory.getProtein(), intakeHistory.getFat(),
        intakeHistory.getCarbs(), intakeHistory.getCaloriesGoal(), intakeHistory.getProteinGoal(),
        intakeHistory.getFatGoal(), intakeHistory.getCarbsGoal(), intakeHistory.getUsername());
  }

  private void updateIntakeHistoryFields(IntakeHistory intakeHistory,
      IntakeHistoryRequest intakeHistoryRequest) {
    intakeHistory.setDate(intakeHistoryRequest.date());
    intakeHistory.setCalories(intakeHistoryRequest.calories());
    intakeHistory.setProtein(intakeHistoryRequest.protein());
    intakeHistory.setFat(intakeHistoryRequest.fat());
    intakeHistory.setCarbs(intakeHistoryRequest.carbs());
    intakeHistory.setCaloriesGoal(intakeHistoryRequest.caloriesGoal());
    intakeHistory.setProteinGoal(intakeHistoryRequest.proteinGoal());
    intakeHistory.setFatGoal(intakeHistoryRequest.fatGoal());
    intakeHistory.setCarbsGoal(intakeHistoryRequest.carbsGoal());
    intakeHistory.setUsername(intakeHistoryRequest.username());
  }

  private void checkIfUserExistByUsername(String username) {
    userService.getUserByUsername(
        username); //it throws exception when user with username does not exist
  }

  public IntakeHistoryResponse getIntakeHistoryById(Long id) {
    return mapToIntakeHistoryResponse(intakeHistoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id))));
  }

  @Transactional
  public void deleteIntakeHistoryById(Long id) {
    if (!intakeHistoryRepository.existsById(id)) {
      throw new EntityNotFoundException(generateNotFoundMessage(id));
    }
    intakeHistoryRepository.deleteById(id);
  }

  @Transactional
  public IntakeHistoryResponse addIntakeHistory(IntakeHistoryRequest intakeHistoryRequest) {
    checkIfUserExistByUsername(intakeHistoryRequest.username());
    boolean isDateUnique = intakeHistoryRepository.getAllByDate(intakeHistoryRequest.date())
        .stream().map(IntakeHistory::getUsername)
        .noneMatch(s -> s.equals(intakeHistoryRequest.username()));
    if (!isDateUnique) {
      throw new IllegalArgumentException("Date must be unique");
    }
    IntakeHistory intakeHistory = new IntakeHistory();
    updateIntakeHistoryFields(intakeHistory, intakeHistoryRequest);
    intakeHistory = intakeHistoryRepository.save(intakeHistory);
    return mapToIntakeHistoryResponse(intakeHistory);
  }

  @Transactional
  public IntakeHistoryResponse updateIntakeHistoryById(Long id,
      @Valid IntakeHistoryRequest intakeHistoryRequest) {
    IntakeHistory intakeHistory = intakeHistoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    updateIntakeHistoryFields(intakeHistory, intakeHistoryRequest);
    intakeHistory = intakeHistoryRepository.save(intakeHistory);
    return mapToIntakeHistoryResponse(intakeHistory);
  }

  @Transactional
  public void deleteHistoryByUsername(String username) {
    intakeHistoryRepository.deleteIntakeHistoriesByUsername(username);
  }

  public List<IntakeHistoryResponse> getAllIntakeHistories() {
    return intakeHistoryRepository.findAll().stream().map(this::mapToIntakeHistoryResponse)
        .toList();
  }
}
