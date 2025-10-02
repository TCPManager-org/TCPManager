package org.tcpmanager.tcpmanager.calories.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class CaloriesExceptionHandler {
  @ExceptionHandler({EntityNotFoundException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
    return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
  }
  @ExceptionHandler({IllegalArgumentException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
  }
}
