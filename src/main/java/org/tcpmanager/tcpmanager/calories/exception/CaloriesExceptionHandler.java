package org.tcpmanager.tcpmanager.calories.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.tcpmanager.tcpmanager.ErrorResponse;

@ControllerAdvice
@RequiredArgsConstructor
public class CaloriesExceptionHandler {

  @ExceptionHandler({EntityNotFoundException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
    return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
  }
}
