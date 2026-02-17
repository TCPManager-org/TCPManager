package org.tcpmanager.tcpmanager.gateway;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ApiResponse(
      responseCode = "400",
      description = "Bad Request",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
  )
  public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
        errors.toString().substring(1, 2).toUpperCase() + errors.toString()
            .substring(2, errors.toString().length() - 1).replace("=", " "));
  }

  @ExceptionHandler({EntityNotFoundException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ApiResponse(
      responseCode = "404",
      description = "Not Found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
  )
  public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
    return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
  }

  @ExceptionHandler({IllegalArgumentException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ApiResponse(
      responseCode = "400",
      description = "Bad Request",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
  )
  public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
  }

  @ExceptionHandler({SecurityException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ApiResponse(
      responseCode = "403",
      description = "Forbidden",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
  )
  public ErrorResponse handleSecurityException(IllegalArgumentException ex) {
    return new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
  }
}
