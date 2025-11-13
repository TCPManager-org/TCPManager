package org.tcpmanager.tcpmanager.calories.day;

import jakarta.persistence.EntityNotFoundException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.day.dto.DayMealPatch;
import org.tcpmanager.tcpmanager.calories.day.dto.DayMealRequest;
import org.tcpmanager.tcpmanager.calories.day.dto.DayMealResponse;
import org.tcpmanager.tcpmanager.calories.day.dto.DayResponse;
import org.tcpmanager.tcpmanager.calories.day.models.Day;
import org.tcpmanager.tcpmanager.calories.day.models.DayMeal;
import org.tcpmanager.tcpmanager.calories.meal.MealRepository;
import org.tcpmanager.tcpmanager.calories.meal.MealService;
import org.tcpmanager.tcpmanager.calories.meal.models.Meal;
import org.tcpmanager.tcpmanager.user.User;
import org.tcpmanager.tcpmanager.user.UserRepository;
import org.tcpmanager.tcpmanager.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DayService {

  private final DayRepository dayRepository;
  private final MealRepository mealRepository;
  private final UserRepository userRepository;

  private static DayResponse mapToDayResponse(Day day) {
    List<DayMealResponse> dayMealResponses = day.getDayMeals().stream().map(
        dayMeal -> new DayMealResponse(dayMeal.getId(), dayMeal.getWeight(), dayMeal.getMealType(),
            MealService.mapToMealResponse(dayMeal.getMeal()))).toList();
    return new DayResponse(day.getId(), day.getDate(), dayMealResponses);
  }


  public static String generateNotFoundMessage(Date date) {
    return "Day with date " + date + " not found";
  }

  public static String generateNotFoundMessage(Date date, String username) {
    return "Day with date " + date + " does not belong to user " + username;
  }

  public static String generateNotFoundMessage(Long dayMealId, Date date) {
    return "DayMeal with id " + dayMealId + " and " + date + " not found";
  }

  private DayMeal mapToDayMeals(Day day, DayMealRequest dayMealRequest) {
    DayMeal dayMeal = new DayMeal();
    dayMeal.setDay(day);
    Meal foundMeal = mealRepository.findById((dayMealRequest.mealId())).orElseThrow(
        () -> new EntityNotFoundException(
            MealService.generateNotFoundMessage(dayMealRequest.mealId())));
    dayMeal.setMeal(foundMeal);
    dayMeal.setMealType(dayMealRequest.mealType());
    dayMeal.setWeight(dayMealRequest.weight());
    return dayMeal;
  }

  public List<DayResponse> getAllDays() {
    return dayRepository.findAll().stream().map(DayService::mapToDayResponse).toList();
  }

  @Transactional
  public void deleteByDate(String username, Date date) {
    if (dayRepository.findByDate(date).isEmpty()) {
      throw new EntityNotFoundException(generateNotFoundMessage(date));
    }
    Day day = dayRepository.findByDate(date).stream()
        .filter(d -> d.getUser().getUsername().equals(username)).findFirst()
        .orElseThrow(() -> new IllegalArgumentException(generateNotFoundMessage(date, username)));
    dayRepository.delete(day);
  }

  @Transactional
  public DayResponse addMealToDay(DayMealRequest dayMealRequest) {
    User user = userRepository.findByUsername(dayMealRequest.username()).orElseThrow(
        () -> new EntityNotFoundException(
            UserService.generateNotFoundMessage(dayMealRequest.username())));
    Optional<Day> dayOptional = dayRepository.findByDateAndUserUsername(dayMealRequest.date(),
        dayMealRequest.username());
    if (dayOptional.isEmpty()) {
      Day day = new Day();
      day.setDate(dayMealRequest.date());
      day.setUser(user);
      day.setDayMeals(Set.of(mapToDayMeals(day, dayMealRequest)));
      Day savedDay = dayRepository.save(day);
      return mapToDayResponse(savedDay);
    }
    Day day = dayOptional.get();
    Set<DayMeal> dayMeals = day.getDayMeals();
    dayMeals.add(mapToDayMeals(day, dayMealRequest));
    day.setDayMeals(dayMeals);
    dayRepository.save(day);
    return mapToDayResponse(day);
  }

  @Transactional
  public void deleteMealFromDay(Date date, Long dayMealId, String username) {
    Day day = getDay(date, dayMealId, username);
    day.getDayMeals().removeIf(dayMeal -> dayMeal.getId() == dayMealId);
    dayRepository.save(day);
  }

  @Transactional
  public DayResponse updateMealFromDay(Date date, Long dayMealId, String username,
      DayMealPatch dayMealPatch) {
    Day day = getDay(date, dayMealId, username);
    day.getDayMeals().forEach(dayMeal -> {
      if (dayMeal.getId() == (dayMealId)) {
        if (dayMealPatch.mealId() != null) {
          Meal foundMeal = mealRepository.findById(dayMealPatch.mealId()).orElseThrow(
              () -> new EntityNotFoundException(
                  MealService.generateNotFoundMessage(dayMealPatch.mealId())));
          dayMeal.setMeal(foundMeal);
        }
        if (dayMealPatch.weight() != null) {
          dayMeal.setWeight(dayMealPatch.weight());
        }
        if (dayMealPatch.mealType() != null) {
          dayMeal.setMealType(dayMealPatch.mealType());
        }
      }
    });
    dayRepository.save(day);
    return mapToDayResponse(day);
  }

  private Day getDay(Date date, Long dayMealId, String username) {
    if (dayRepository.findByDate(date).isEmpty()) {
      throw new EntityNotFoundException(generateNotFoundMessage(date));
    }
    Day day = dayRepository.findByDate(date).stream()
        .filter(d -> d.getUser().getUsername().equals(username)).findFirst()
        .orElseThrow(() -> new IllegalArgumentException(generateNotFoundMessage(date, username)));
    if (day.getDayMeals().stream().anyMatch(dayMeal -> dayMeal.getId() == dayMealId)) {
      throw new EntityNotFoundException(generateNotFoundMessage(dayMealId, date));
    }
    return day;
  }
}
