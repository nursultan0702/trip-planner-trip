package com.tripplannertrip.controller;

import com.tripplannertrip.model.DateSortType;
import com.tripplannertrip.model.TripRecord;
import com.tripplannertrip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @GetMapping("/trip/{id}")
    public ResponseEntity<TripRecord> getTrip(@PathVariable int id) {
        var trip = tripService.getById(id);
        return ResponseEntity.ok().body(trip);
    }

    @GetMapping("/trips")
    public ResponseEntity<List<TripRecord>> getTrips(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @RequestParam List<String> emails,
            @RequestParam(defaultValue = "date_asc") DateSortType sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return null;
    }

    @PostMapping("/trip")
    public ResponseEntity<TripRecord> createTrip(@RequestBody TripRecord trip) {
        var createdTrip = tripService.createTrip(trip);
        return new ResponseEntity<>(createdTrip, HttpStatus.CREATED);
    }

    @PatchMapping("/trip/{tripId}")
    public ResponseEntity<TripRecord> updateTrip(@PathVariable long tripId, @RequestBody TripRecord trip) {
        return null;
    }

    @DeleteMapping("/trip/{tripId}")
    public ResponseEntity<TripRecord> deleteTrip(@PathVariable long tripId) {
        return null;
    }

}
