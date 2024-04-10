package com.tripplannertrip.controller;

import com.tripplannertrip.exception.PlaceNotFoundException;
import com.tripplannertrip.exception.TripNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({PlaceNotFoundException.class, TripNotFoundException.class})
  public ResponseEntity<Object> placeNotFound(Exception ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }
}
