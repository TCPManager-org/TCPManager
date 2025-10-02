package org.tcpmanager.tcpmanager.calories.meal;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealRequest;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealService {

  private final MealRepository mealRepository;

  public List<MealResponse> getAll() {
    return mealRepository.findAll().stream()
        .map(meal -> new MealResponse(meal.getId(), meal.getName())).toList();
  }

  public MealResponse getById(Long id) {
    Meal meal = mealRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Meal with id " + id + " not found"));
    return new MealResponse(meal.getId(), meal.getName());
  }

  @Transactional
  public void deleteById(Long id) {
    if (!mealRepository.existsById(id)) {
      throw new EntityNotFoundException("Meal with id " + id + " not found");
    }

    mealRepository.deleteById(id);
  }

  @Transactional
  public MealResponse updateById(Long id, MealRequest mealRequest) {
    Meal meal = mealRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Meal with id " + id + " not found"));
    meal.setName(mealRequest.name());
    mealRepository.save(meal);
    return new MealResponse(meal.getId(), meal.getName());
  }

  @Transactional
  public MealResponse add(MealRequest mealRequest) {
    Meal meal = new Meal();
    meal.setName(mealRequest.name());
    meal = mealRepository.save(meal);
    return new MealResponse(meal.getId(), meal.getName());
  }

}
