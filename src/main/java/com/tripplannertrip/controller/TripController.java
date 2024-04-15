package com.tripplannertrip.controller;

import com.tripplannertrip.model.DateSortType;
import com.tripplannertrip.model.TripRecord;
import com.tripplannertrip.service.TripService;
import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/trips")
@RequiredArgsConstructor
public class TripController {

  private final TripService tripService;

  @GetMapping("/{id}")
  public ResponseEntity<TripRecord> getTrip(@PathVariable int id) {
    var trip = tripService.getById(id);
    return ResponseEntity.ok().body(trip);
  }

  @GetMapping
  public ResponseEntity<List<TripRecord>> getTrips(
      @RequestParam(required = false) LocalDateTime startDate,
      @RequestParam(required = false) LocalDateTime endDate,
      @RequestParam(required = false) Set<String> emails,
      @RequestParam(defaultValue = "date_asc") DateSortType sort,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int limit) {
    List<TripRecord> trips =
        tripService.getTripRecordByFilter(startDate, endDate, emails, sort, page, limit);
    return ResponseEntity.ok().body(trips);
  }

  @PostMapping
  public ResponseEntity<TripRecord> createTrip(@RequestBody TripRecord trip, @AuthenticationPrincipal
  User user) {
    var createdTrip = tripService.createTrip(user, trip);
    return new ResponseEntity<>(createdTrip, HttpStatus.CREATED);
  }

  @PutMapping("/{tripId}")
  public ResponseEntity<TripRecord> updateTrip(@PathVariable long tripId,
                                               @RequestBody TripRecord trip) {
    var updatedTrip = tripService.updateTrip(tripId, trip);
    return ResponseEntity.ok().body(updatedTrip);
  }

  @DeleteMapping("/{tripId}")
  public ResponseEntity<TripRecord> deleteTrip(@PathVariable long tripId) {
    tripService.deleteTrip(tripId);
    return ResponseEntity.noContent().build();
  }


  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(DateSortType.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        for (DateSortType type : DateSortType.values()) {
          if (type.getValue().equalsIgnoreCase(text)) {
            setValue(type);
            return;
          }
        }
        throw new IllegalArgumentException("Invalid value for DateSortType: " + text);
      }
    });
  }
}
