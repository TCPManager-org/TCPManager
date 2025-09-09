package org.tcpmanager.tcpmanager.calories.meal.exception;

import jakarta.persistence.EntityNotFoundException;

public class MealNotFoundException extends EntityNotFoundException {
  public MealNotFoundException(String message) {
    super(message);
  }
}
