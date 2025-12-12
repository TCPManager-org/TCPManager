package org.tcpmanager.tcpmanager.intakehistory;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.tcpmanager.tcpmanager.intakehistory.dto.IntakeHistoryPatch;
import org.tcpmanager.tcpmanager.intakehistory.dto.IntakeHistoryRequest;
import org.tcpmanager.tcpmanager.intakehistory.dto.IntakeHistoryResponse;
import org.tcpmanager.tcpmanager.user.User;
import org.tcpmanager.tcpmanager.user.UserRepository;
import org.tcpmanager.tcpmanager.user.UserService;
import org.tcpmanager.tcpmanager.user.events.MealAddedEvent;
import org.tcpmanager.tcpmanager.user.events.MealDeletedEvent;

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
  public IntakeHistoryResponse addIntakeHistory(IntakeHistoryRequest intakeHistoryRequest,
      String username) {
    User user = userRepository.findByUsername(username).orElseThrow(
        () -> new EntityNotFoundException(
            UserService.generateNotFoundMessage(username)));
    boolean isDateUnique = intakeHistoryRepository.getAllByDate(intakeHistoryRequest.date())
        .stream().map(IntakeHistory::getUser).map(User::getUsername)
        .noneMatch(s -> s.equals(username));
    if (!isDateUnique) {
      throw new IllegalArgumentException("Date must be unique");
    }
    IntakeHistory intakeHistory = mapToIntakeHistory(intakeHistoryRequest, user);
    intakeHistory = intakeHistoryRepository.save(intakeHistory);
    return mapToIntakeHistoryResponse(intakeHistory);
  }

  @Transactional
  public IntakeHistoryResponse updateIntakeHistoryById(Long id,
      @Valid IntakeHistoryPatch intakeHistoryPatch) {
    IntakeHistory intakeHistory = intakeHistoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    if (intakeHistoryPatch.calories() != null) {
      intakeHistory.setCalories(intakeHistoryPatch.calories());
    }
    if (intakeHistoryPatch.protein() != null) {
      intakeHistory.setProtein(intakeHistoryPatch.protein());
    }
    if (intakeHistoryPatch.fat() != null) {
      intakeHistory.setFat(intakeHistoryPatch.fat());
    }
    if (intakeHistoryPatch.carbs() != null) {
      intakeHistory.setCarbs(intakeHistoryPatch.carbs());
    }
    if (intakeHistoryPatch.caloriesGoal() != null) {
      intakeHistory.setCaloriesGoal(intakeHistoryPatch.caloriesGoal());
    }
    if (intakeHistoryPatch.proteinGoal() != null) {
      intakeHistory.setProteinGoal(intakeHistoryPatch.proteinGoal());
    }
    if (intakeHistoryPatch.fatGoal() != null) {
      intakeHistory.setFatGoal(intakeHistoryPatch.fatGoal());
    }
    if (intakeHistoryPatch.carbsGoal() != null) {
      intakeHistory.setCarbsGoal(intakeHistoryPatch.carbsGoal());
    }
    intakeHistory = intakeHistoryRepository.save(intakeHistory);
    return mapToIntakeHistoryResponse(intakeHistory);
  }

  @Transactional
  public void deleteHistoryByUsername(String username) {
    intakeHistoryRepository.deleteIntakeHistoriesByUserUsername(username);
  }

  public List<IntakeHistoryResponse> getAllIntakeHistoriesByUsername(String username) {
    return intakeHistoryRepository.getAllByUserUsername(username).stream()
        .map(this::mapToIntakeHistoryResponse).toList();
  }

  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  void on(MealAddedEvent event) {
    IntakeHistory intakeHistory = intakeHistoryRepository.getByDate(event.date()).stream()
        .filter(ih ->
            ih.getUser().getUsername().equals(event.username()))
        .findFirst().orElseGet(() -> getNewIntakeHistory(event));
    intakeHistory.setCalories(intakeHistory.getCalories().add(event.calories()));
    intakeHistory.setProtein(intakeHistory.getProtein().add(event.protein()));
    intakeHistory.setFat(intakeHistory.getFat().add(event.fat()));
    intakeHistory.setCarbs(intakeHistory.getCarbs().add(event.carbs()));
    intakeHistoryRepository.save(intakeHistory);
  }

  private IntakeHistory getNewIntakeHistory(MealAddedEvent event) {
    IntakeHistory intakeHistoryPrior = intakeHistoryRepository
        .getByDate(Date.valueOf(event.date().toLocalDate().minusDays(1))).stream()
        .findFirst().orElseThrow(
            () -> new IllegalStateException("There is no prior intake history for user "
                + event.username()));
    User user = userRepository.findByUsername(event.username()).orElseThrow(
        () -> new EntityNotFoundException(
            UserService.generateNotFoundMessage(event.username())));
    return new IntakeHistory(null, event.date(), BigDecimal.ZERO, BigDecimal.ZERO,
        BigDecimal.ZERO, BigDecimal.ZERO, intakeHistoryPrior.getCaloriesGoal(),
        intakeHistoryPrior.getProteinGoal(), intakeHistoryPrior.getFatGoal(),
        intakeHistoryPrior.getCarbsGoal(), user);
  }

  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  void on(MealDeletedEvent event) {
    IntakeHistory intakeHistory = intakeHistoryRepository.getByDate(event.date()).stream()
        .filter(ih ->
            ih.getUser().getUsername().equals(event.username()))
        .findFirst()
        .orElseThrow(
            () -> new EntityNotFoundException("Intake history for user " + event.username()
                + " on date " + event.date() + " not found"));
    intakeHistory.setCalories(intakeHistory.getCalories().subtract(event.calories()));
    intakeHistory.setProtein(intakeHistory.getProtein().subtract(event.protein()));
    intakeHistory.setFat(intakeHistory.getFat().subtract(event.fat()));
    intakeHistory.setCarbs(intakeHistory.getCarbs().subtract(event.carbs()));
    intakeHistoryRepository.save(intakeHistory);
  }
}
