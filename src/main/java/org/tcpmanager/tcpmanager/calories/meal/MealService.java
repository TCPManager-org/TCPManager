package org.tcpmanager.tcpmanager.calories.meal;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealPatch;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealRequest;
import org.tcpmanager.tcpmanager.calories.meal.dto.MealResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealService {

  private final MealRepository mealRepository;

  private static String generateNotFoundMessage(Long id) {
    return "Meal with id " + id + " not found";
  }

  public List<MealResponse> getAll() {
    return mealRepository.findAll().stream()
        .map(meal -> new MealResponse(meal.getId(), meal.getName())).toList();
  }

  public MealResponse getById(Long id) {
    Meal meal = mealRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    return new MealResponse(meal.getId(), meal.getName());
  }

  @Transactional
  public void deleteById(Long id) {
    if (!mealRepository.existsById(id)) {
      throw new EntityNotFoundException(generateNotFoundMessage(id));
    }

    mealRepository.deleteById(id);
  }

  @Transactional
  public MealResponse updateById(Long id, MealPatch mealPatch) {
    Meal meal = mealRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(generateNotFoundMessage(id)));
    meal.setName(mealPatch.name().strip());
    mealRepository.save(meal);
    return new MealResponse(meal.getId(), meal.getName());
  }

  @Transactional
  public MealResponse add(MealRequest mealRequest) {
    Meal meal = new Meal();
    meal.setName(mealRequest.name().strip());
    meal = mealRepository.save(meal);
    return new MealResponse(meal.getId(), meal.getName());
  }
}
