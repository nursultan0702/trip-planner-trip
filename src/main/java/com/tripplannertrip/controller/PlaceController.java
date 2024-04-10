package com.tripplannertrip.controller;

import com.tripplannertrip.model.PlaceRecord;
import com.tripplannertrip.service.PlaceService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/places")
@RequiredArgsConstructor
public class PlaceController {

  private final PlaceService placeService;

  @GetMapping("/trip/{tripId}")
  public ResponseEntity<List<PlaceRecord>> getPlacesByTripId(@PathVariable Long tripId) {
    List<PlaceRecord> places = placeService.getAllPlacesByTripId(tripId);
    return ResponseEntity.ok(places);
  }

  @GetMapping
  public ResponseEntity<List<PlaceRecord>> getPlaceByFilter(
      @RequestParam(required = false) LocalDateTime startDate,
      @RequestParam(required = false) LocalDateTime endDate,
      @RequestParam(required = false) Set<String> emails,
      @RequestParam(required = false) String country,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int limit) {
    List<PlaceRecord> placesByCriteria =
        placeService.getPlacesByCriteria(startDate, endDate, emails, country, page, limit);
    return ResponseEntity.ok(placesByCriteria);

  }

  @GetMapping("/{id}")
  public ResponseEntity<PlaceRecord> getPlaceById(@PathVariable Long id) {
    PlaceRecord place = placeService.getPlaceById(id);
    return ResponseEntity.ok(place);
  }

  @PostMapping
  public ResponseEntity<PlaceRecord> createPlace(@RequestBody PlaceRecord placeRecord) {
    PlaceRecord createdPlace = placeService.createPlace(placeRecord);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPlace);
  }

  @PutMapping("/{id}")
  public ResponseEntity<PlaceRecord> updatePlace(@PathVariable Long id,
                                                 @RequestBody PlaceRecord placeRecord) {
    PlaceRecord updatedPlace = placeService.updatePlace(id, placeRecord);
    if (updatedPlace != null) {
      return ResponseEntity.ok(updatedPlace);
    }
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
    placeService.deletePlace(id);
    return ResponseEntity.noContent().build();
  }
}
