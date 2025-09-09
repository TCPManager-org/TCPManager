package org.tcpmanager.tcpmanager.calories.meal.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class MealExceptionHandler {
  @ExceptionHandler({MealNotFoundException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleMealNotFoundException(MealNotFoundException ex) {
    return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
  }
  @ExceptionHandler({IllegalMealNameException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleIllegalMealNameException(IllegalMealNameException ex) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
  }
}
