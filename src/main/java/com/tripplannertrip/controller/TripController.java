package com.tripplannertrip.controller;

import com.tripplannertrip.model.DateSortType;
import com.tripplannertrip.model.TripRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TripController {

    @GetMapping("/trip/{id}")
    public ResponseEntity<TripRecord> getTrip(@PathVariable int id) {
        return null;
    }

    @GetMapping("/trips")
    public ResponseEntity<List<TripRecord>> getTrips(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @RequestParam String country,
            @RequestParam List<String> emails,
            @RequestParam(defaultValue = "date_asc") DateSortType sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return null;
    }

    @PostMapping("/trip")
    public ResponseEntity<TripRecord> createTrip(@RequestBody TripRecord trip) {
        return null;
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
