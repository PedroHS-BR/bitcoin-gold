package com.hidra.bitcoingold.handler;

import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.exception.ExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionDetails> handlerBadRequestException(BadRequestException e) {
        return new ResponseEntity<>(
                ExceptionDetails.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .title("Bad Request Exception")
                        .details(e.getMessage())
                        .developerMessage(e.getClass().getName())
                        .build(), HttpStatus.BAD_REQUEST
        );
    }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<ExceptionDetails> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
       return new ResponseEntity<>(
               ExceptionDetails.builder()
                       .timestamp(LocalDateTime.now())
                       .status(HttpStatus.BAD_REQUEST.value())
                       .title("Bad Request Exception")
                       .details(e.getMessage())
                       .developerMessage(e.getClass().getName())
                       .build(), HttpStatus.BAD_REQUEST);
   }
}
