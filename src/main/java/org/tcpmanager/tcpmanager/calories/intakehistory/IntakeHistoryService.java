package org.tcpmanager.tcpmanager.calories.intakehistory;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.intakehistory.dto.IntakeHistoryRequest;
import org.tcpmanager.tcpmanager.calories.intakehistory.dto.IntakeHistoryResponse;
import org.tcpmanager.tcpmanager.user.User;
import org.tcpmanager.tcpmanager.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntakeHistoryService {

  private final IntakeHistoryRepository intakeHistoryRepository;
  private final UserRepository userRepository;

  private static String generateNotFoundMessage(Long id) {
    return "Intake history with id " + id + " not found";
  }

  private static IntakeHistory mapToIntakeHistory(IntakeHistoryRequest intakeHistoryRequest,
      User user) {
    IntakeHistory intakeHistory = new IntakeHistory();
    intakeHistory.setUser(user);
    intakeHistory.setProtein(intakeHistoryRequest.protein());
    intakeHistory.setFat(intakeHistoryRequest.fat());
    intakeHistory.setCarbs(intakeHistoryRequest.carbs());
    intakeHistory.setDate(intakeHistoryRequest.date());
    intakeHistory.setCalories(intakeHistoryRequest.calories());
    intakeHistory.setCaloriesGoal(intakeHistoryRequest.caloriesGoal());
    intakeHistory.setProteinGoal(intakeHistoryRequest.proteinGoal());
    intakeHistory.setFatGoal(intakeHistoryRequest.fatGoal());
    intakeHistory.setCarbsGoal(intakeHistoryRequest.carbsGoal());
    return intakeHistory;
  }

  private IntakeHistoryResponse mapToIntakeHistoryResponse(IntakeHistory intakeHistory) {
    return new IntakeHistoryResponse(intakeHistory.getId(), intakeHistory.getDate(),
        intakeHistory.getCalories(), intakeHistory.getProtein(), intakeHistory.getFat(),
        intakeHistory.getCarbs(), intakeHistory.getCaloriesGoal(), intakeHistory.getProteinGoal(),
        intakeHistory.getFatGoal(), intakeHistory.getCarbsGoal(),
        intakeHistory.getUser().getUsername());
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
    intakeHistory.setUser(userRepository.findByUsername(intakeHistoryRequest.username())
        .orElseThrow(() -> new EntityNotFoundException(
            "User with username " + intakeHistoryRequest.username() + " not found")));
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
    User user = userRepository.findByUsername(intakeHistoryRequest.username()).orElseThrow(
        () -> new EntityNotFoundException(
            "User with username " + intakeHistoryRequest.username() + " not found"));
    boolean isDateUnique = intakeHistoryRepository.getAllByDate(intakeHistoryRequest.date())
        .stream().map(IntakeHistory::getUser).map(User::getUsername)
        .noneMatch(s -> s.equals(intakeHistoryRequest.username()));
    if (!isDateUnique) {
      throw new IllegalArgumentException("Date must be unique");
    }
    IntakeHistory intakeHistory = mapToIntakeHistory(intakeHistoryRequest, user);
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
    intakeHistoryRepository.deleteIntakeHistoriesByUserUsername(username);
  }

  public List<IntakeHistoryResponse> getAllIntakeHistories() {
    return intakeHistoryRepository.findAll().stream().map(this::mapToIntakeHistoryResponse)
        .toList();
  }
}
