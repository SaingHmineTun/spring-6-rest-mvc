package guru.springframework.spring6restmvc.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity handleValidationExceptions(MethodArgumentNotValidException exception) {
        List errorList = exception.getBindingResult().getFieldErrors().stream().map(fieldError -> {
                    Map<String, String> errors = new HashMap<>();
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage());
                    return errors;
                }
        ).toList();
        return ResponseEntity.badRequest().body(errorList);
    }

    @ExceptionHandler(TransactionSystemException.class)
    ResponseEntity handleJpaViolation(TransactionSystemException exception) {
        var resBuilder = ResponseEntity.badRequest();

        if (exception.getCause().getCause() instanceof ConstraintViolationException) {
            var ex = (ConstraintViolationException) exception.getCause().getCause();
            List errors = ex.getConstraintViolations().stream().map(constraintViolation -> {

                        Map<String, String> errorMap = new HashMap<>();
                        errorMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
                        return errorMap;

                    }
            ).toList();
            return resBuilder.body(errors);
        }

        return resBuilder.build();
    }

}
