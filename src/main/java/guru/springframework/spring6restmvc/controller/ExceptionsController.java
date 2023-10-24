package guru.springframework.spring6restmvc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionsController {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> notFoundException() {
        return ResponseEntity.notFound().build();
    }

}
