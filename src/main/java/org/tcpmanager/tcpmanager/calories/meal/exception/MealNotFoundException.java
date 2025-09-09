package org.tcpmanager.tcpmanager.calories.meal.exception;

public class MealNotFoundException extends RuntimeException {

  public MealNotFoundException(Long id) {
    super("Meal with id " + id + " not found");
  }
}
