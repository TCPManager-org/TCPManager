package org.tcpmanager.tcpmanager.calories.day;

import jakarta.persistence.EntityNotFoundException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.day.dto.DayMealRequest;
import org.tcpmanager.tcpmanager.calories.day.dto.DayMealResponse;
import org.tcpmanager.tcpmanager.calories.day.dto.DayResponse;
import org.tcpmanager.tcpmanager.calories.day.models.Day;
import org.tcpmanager.tcpmanager.calories.day.models.DayMeal;
import org.tcpmanager.tcpmanager.calories.meal.MealRepository;
import org.tcpmanager.tcpmanager.calories.meal.models.Meal;
import org.tcpmanager.tcpmanager.user.User;
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

  private DayMeal mapToDayMeals(Day day, DayMealRequest dayMealRequest) {
    DayMeal dayMeal = new DayMeal();
    dayMeal.setDay(day);
    Meal foundMeal = mealRepository.findById(dayMealRequest.mealId()).orElseThrow(
        () -> new EntityNotFoundException(
            "Meal with id " + dayMealRequest.mealId() + " not found"));
    dayMeal.setMeal(foundMeal);
    dayMeal.setMealType(dayMealRequest.mealType());
    dayMeal.setWeight(dayMealRequest.weight());
    return dayMeal;
  }

  public List<DayResponse> getAllDays() {
    return dayRepository.findAll().stream().map(DayService::mapToDayResponse).toList();
  }

  @Transactional
  public void deleteByDate(Date date) {
    if (!dayRepository.existsByDate(date)) {
      throw new EntityNotFoundException("Day with date " + date + " not found");
    } else {
      dayRepository.deleteByDate(date);
    }
  }

  @Transactional
  public DayResponse addMealToDay(DayMealRequest dayMealRequest) {
    Optional<User> userOptional = userRepository.findById(dayMealRequest.userId());
    if(userOptional.isEmpty()){
      throw new EntityNotFoundException("User with id " + dayMealRequest.userId() + " not found");
    }
    User user = userOptional.get();
    Optional<Day> dayOptional = dayRepository.findByDateAndUserId(dayMealRequest.date(),
        dayMealRequest.userId());
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

  public void deleteMealFromDay(DayMealRequest dayMealRequest) {
    if (!userRepository.existsById(dayMealRequest.userId())) {
      throw new EntityNotFoundException("User with id " + dayMealRequest.userId() + " not found");
    }
    Optional<Day> dayOptional = dayRepository.findByDateAndUserId(dayMealRequest.date(),
        dayMealRequest.userId());
    Optional<Meal> mealOptional = mealRepository.findById(dayMealRequest.mealId());
    if (dayOptional.isEmpty()) {
      throw new EntityNotFoundException("Day with date " + dayMealRequest.date() + " not found");
    }
    if (mealOptional.isEmpty()) {
      throw new EntityNotFoundException("Meal with id " + dayMealRequest.mealId() + " not found");
    }
    Day day = dayOptional.get();
    Set<DayMeal> dayMeals = day.getDayMeals();
    dayMeals.remove(mapToDayMeals(day, dayMealRequest));
  }
}
