package org.tcpmanager.tcpmanager.statistics.intakehistory;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.tcpmanager.tcpmanager.statistics.intakehistory.dto.IntakeHistoryPatch;
import org.tcpmanager.tcpmanager.statistics.intakehistory.dto.IntakeHistoryRequest;
import org.tcpmanager.tcpmanager.statistics.intakehistory.dto.IntakeHistoryResponse;
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

  private static String generateNotFoundMessage(String username, Date date) {
    return "Intake history for user " + username + " on date " + date + " not found";
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

  private static IntakeHistoryResponse mapToIntakeHistoryResponse(IntakeHistory intakeHistory) {
    return new IntakeHistoryResponse(intakeHistory.getId(), intakeHistory.getDate(),
        intakeHistory.getCalories(), intakeHistory.getProtein(), intakeHistory.getFat(),
        intakeHistory.getCarbs(), intakeHistory.getCaloriesGoal(), intakeHistory.getProteinGoal(),
        intakeHistory.getFatGoal(), intakeHistory.getCarbsGoal(),
        intakeHistory.getUser().getUsername());
  }

  public List<IntakeHistoryResponse> getAllIntakeHistoriesByUsername(String username) {
    return intakeHistoryRepository.getAllByUserUsername(username).stream()
        .map(IntakeHistoryService::mapToIntakeHistoryResponse)
        .sorted(Comparator.comparing(IntakeHistoryResponse::date)).toList();
  }

  public IntakeHistoryResponse getIntakeHistoryById(Long id, String username) {
    User user = userRepository.findByUsername(username).orElseThrow(
        () -> new EntityNotFoundException(UserService.generateNotFoundMessage(username)));
    IntakeHistory ih = intakeHistoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    if (!ih.getUser().getUsername().equals(user.getUsername())) {
      throw new EntityNotFoundException(generateNotFoundMessage(id));
    }
    return mapToIntakeHistoryResponse(ih);
  }

  @Transactional
  public IntakeHistoryResponse addIntakeHistory(IntakeHistoryRequest intakeHistoryRequest,
      String username) {
    User user = userRepository.findByUsername(username).orElseThrow(
        () -> new EntityNotFoundException(UserService.generateNotFoundMessage(username)));
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
    intakeHistory = updateIntakeHistory(intakeHistory, intakeHistoryPatch);
    return mapToIntakeHistoryResponse(intakeHistory);
  }

  @Transactional
  public IntakeHistoryResponse updateIntakeHistoryByDate(String date,
      IntakeHistoryPatch intakeHistoryPatch, String name) {
    User user = userRepository.findByUsername(name)
        .orElseThrow(() -> new EntityNotFoundException(UserService.generateNotFoundMessage(name)));
    Date sqlDate;
    try {
      sqlDate = Date.valueOf(date);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid date format, expected yyyy-MM-dd");
    }
    IntakeHistory intakeHistory = intakeHistoryRepository.getByDate(sqlDate).stream()
        .filter(ih -> ih.getUser().getUsername().equals(user.getUsername())).findFirst()
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(name, sqlDate)));
    intakeHistory = updateIntakeHistory(intakeHistory, intakeHistoryPatch);
    return mapToIntakeHistoryResponse(intakeHistory);
  }

  @Transactional
  public void deleteIntakeHistoryById(Long id) {
    if (!intakeHistoryRepository.existsById(id)) {
      throw new EntityNotFoundException(generateNotFoundMessage(id));
    }
    intakeHistoryRepository.deleteById(id);
  }

  @Transactional
  public void deleteHistoryByUsername(String username) {
    intakeHistoryRepository.deleteIntakeHistoriesByUserUsername(username);
  }

  private IntakeHistory updateIntakeHistory(IntakeHistory intakeHistory,
      IntakeHistoryPatch intakeHistoryPatch) {
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
    return intakeHistoryRepository.save(intakeHistory);
  }

  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  void on(MealAddedEvent event) {
    Optional<IntakeHistory> intakeHistoryOptional = intakeHistoryRepository.getByDate(event.date())
        .stream().filter(ih -> ih.getUser().getUsername().equals(event.username())).findFirst();
    IntakeHistory intakeHistory;
    if (intakeHistoryOptional.isPresent()) {
      intakeHistory = intakeHistoryOptional.get();
    } else {
      IntakeHistory intakeHistoryPrior = intakeHistoryRepository.getByDate(
          Date.valueOf(event.date().toLocalDate().minusDays(1))).stream().findFirst().orElseThrow(
          () -> new IllegalStateException(
              "There is no prior intake history for user " + event.username()));
      User user = userRepository.findByUsername(event.username()).orElseThrow(
          () -> new EntityNotFoundException(UserService.generateNotFoundMessage(event.username())));
      intakeHistory = new IntakeHistory(null, event.date(), BigDecimal.ZERO, BigDecimal.ZERO,
          BigDecimal.ZERO, BigDecimal.ZERO, intakeHistoryPrior.getCaloriesGoal(),
          intakeHistoryPrior.getProteinGoal(), intakeHistoryPrior.getFatGoal(),
          intakeHistoryPrior.getCarbsGoal(), user);
    }
    intakeHistory.setCalories(intakeHistory.getCalories().add(event.calories()));
    intakeHistory.setProtein(intakeHistory.getProtein().add(event.protein()));
    intakeHistory.setFat(intakeHistory.getFat().add(event.fat()));
    intakeHistory.setCarbs(intakeHistory.getCarbs().add(event.carbs()));
    intakeHistoryRepository.save(intakeHistory);
  }

  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  void on(MealDeletedEvent event) {
    IntakeHistory intakeHistory = intakeHistoryRepository.getByDate(event.date()).stream()
        .filter(ih -> ih.getUser().getUsername().equals(event.username())).findFirst().orElseThrow(
            () -> new EntityNotFoundException(
                generateNotFoundMessage(event.username(), event.date())));
    intakeHistory.setCalories(intakeHistory.getCalories().subtract(event.calories()));
    intakeHistory.setProtein(intakeHistory.getProtein().subtract(event.protein()));
    intakeHistory.setFat(intakeHistory.getFat().subtract(event.fat()));
    intakeHistory.setCarbs(intakeHistory.getCarbs().subtract(event.carbs()));
    intakeHistoryRepository.save(intakeHistory);
  }
}
