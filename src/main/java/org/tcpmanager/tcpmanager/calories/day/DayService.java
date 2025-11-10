package org.tcpmanager.tcpmanager.calories.day;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.day.dto.DayMealRequest;
import org.tcpmanager.tcpmanager.calories.day.dto.DayMealResponse;
import org.tcpmanager.tcpmanager.calories.day.dto.DayRequest;
import org.tcpmanager.tcpmanager.calories.day.dto.DayResponse;
import org.tcpmanager.tcpmanager.calories.day.models.Day;
import org.tcpmanager.tcpmanager.calories.day.models.DayMeal;
import org.tcpmanager.tcpmanager.calories.meal.MealRepository;
import org.tcpmanager.tcpmanager.calories.meal.models.Meal;
import org.tcpmanager.tcpmanager.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DayService {

  private final DayRepository dayRepository;
  private final MealRepository mealRepository;
  private final UserRepository userRepository;

  private static DayResponse mapToDayResponse(Day day) {
    List<DayMealResponse> dayMealResponses = day.getDayMeals().stream().map(
        dayMeal -> new DayMealResponse(dayMeal.getMeal().getId(), dayMeal.getWeight(),
            dayMeal.getMealType())).toList();
    return new DayResponse(day.getDate(), dayMealResponses);
  }

  public List<DayResponse> getAllDays() {
    return dayRepository.findAll().stream().map(DayService::mapToDayResponse).toList();
  }

  @Transactional
  public DayResponse addDay(@Valid DayRequest dayRequest) {
    if (!userRepository.existsById(dayRequest.userId())) {
      throw new EntityNotFoundException(
          "User with id " + dayRequest.userId() + " not found");
    }
    if( dayRepository.existsByDateAndUserId(dayRequest.date(), dayRequest.userId())) {
      throw new IllegalArgumentException(
          "Day for date " + dayRequest.date() + " and user with id " + dayRequest.userId()
              + " already exists");
    }
    Day day = new Day();
    day.setDate(dayRequest.date());
    day.setUserId(dayRequest.userId());
    day.setDayMeals(mapToDayMeals(day, dayRequest.dayMeals()));
    Day savedDay = dayRepository.save(day);
    return mapToDayResponse(savedDay);
  }

  private Set<DayMeal> mapToDayMeals(Day day, Set<DayMealRequest> dayMealsRequest) {
    Set<DayMeal> dayMeals = new HashSet<>();
    for (DayMealRequest dayMealRequest : dayMealsRequest) {
      DayMeal dayMeal = new DayMeal();
      dayMeal.setDay(day);
      Meal foundMeal = mealRepository.findById(dayMealRequest.mealId()).orElseThrow(
          () -> new EntityNotFoundException(
              "Meal with id " + dayMealRequest.mealId() + " not found"));
      dayMeal.setMeal(foundMeal);
      dayMeal.setMealType(dayMealRequest.mealType());
      dayMeal.setWeight(dayMealRequest.weight());
      dayMeals.add(dayMeal);
    }
    return dayMeals;
  }
}
