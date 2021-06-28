package tech.blackfall.dicedist.simulation.kernel.adapter;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ErrorHandlingControllerAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseBody
  ResponseEntity<ValidationErrorResponse> onConstraintValidationException(ConstraintViolationException e) {
    List<Violation> violations = e.getConstraintViolations().stream()
        .map(violation -> new Violation(violation.getPropertyPath().toString(), violation.getMessage()))
        .collect(Collectors.toList());
    return ResponseEntity.badRequest()
        .body(new ValidationErrorResponse(violations));
  }

  @Value
  static class ValidationErrorResponse {

    List<Violation> violations;
  }

  @Value
  static class Violation {

    String fieldName;
    String message;
  }
}
