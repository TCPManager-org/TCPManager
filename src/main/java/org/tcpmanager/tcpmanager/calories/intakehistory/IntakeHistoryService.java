package org.tcpmanager.tcpmanager.calories.intakehistory;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tcpmanager.tcpmanager.calories.intakehistory.dto.IntakeHistoryRequest;
import org.tcpmanager.tcpmanager.calories.intakehistory.dto.IntakeHistoryResponse;
import org.tcpmanager.tcpmanager.user.UserService;

@Service
@RequiredArgsConstructor
public class IntakeHistoryService {

  private final IntakeHistoryRepository intakeHistoryRepository;
  private final UserService userService;
  private String generateNotFoundMessage(Long id) {
    return "Intake history with id " + id + " not found";
  }

  private IntakeHistoryResponse mapToIntakeHistoryResponse(IntakeHistory intakeHistory) {
    return new IntakeHistoryResponse(intakeHistory.getId(), intakeHistory.getDate(),
        intakeHistory.getCalories(), intakeHistory.getProtein(), intakeHistory.getFat(),
        intakeHistory.getCarbs(), intakeHistory.getCaloriesGoal(), intakeHistory.getProteinGoal(),
        intakeHistory.getFatGoal(), intakeHistory.getCarbsGoal(),
        intakeHistory.getUsername());
  }

  public IntakeHistoryResponse getIntakeHistoryById(Long id) {
    return mapToIntakeHistoryResponse(intakeHistoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id))));
  }

  public void deleteIntakeHistoryById(Long id) {
    if (!intakeHistoryRepository.existsById(id)) {
      throw new EntityNotFoundException(generateNotFoundMessage(id));
    }
    intakeHistoryRepository.deleteById(id);
  }

  public IntakeHistoryResponse addIntakeHistory(IntakeHistoryRequest intakeHistoryRequest) {
    //TODO: delete history for user if deleted;Dates unique per user
    userService.getUserByUsername(intakeHistoryRequest.username());
    IntakeHistory intakeHistory = new IntakeHistory();
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
    intakeHistory = intakeHistoryRepository.save(intakeHistory);
    return mapToIntakeHistoryResponse(intakeHistory);
  }
}
