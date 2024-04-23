package com.tripplannertrip.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.tripplannertrip.AbstractIntegrationTest;
import com.tripplannertrip.model.PlaceRecord;
import com.tripplannertrip.service.PlaceService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

class PlaceControllerTest extends AbstractIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private PlaceService placeService;

  private String getBaseUri() {
    return "http://localhost:" + port + "/v1/api/places";
  }

  @Test
  void testGetPlaceById() {
    given()
        .port(port)
        .when()
        .get(getBaseUri() + "/{id}", 1L)
        .then()
        .statusCode(200)
        .body("placeId", equalTo(1)); // Assumes place with ID 1 exists
  }

  @Test
  void testCreatePlace() {
    PlaceRecord newPlace = PlaceRecord.builder()
        .country("Italy")
        .city("Rome")
        .name("Colosseum")
        .tripIds(Set.of(1L))
        .images(List.of("image1.jpg", "image2.jpg"))
        .build();

    given()
        .port(port)
        .contentType("application/json")
        .body(newPlace)
        .when()
        .post(getBaseUri())
        .then()
        .statusCode(201)
        .body("placeId", notNullValue());
  }

  @Test
  void testUpdatePlace() {
    PlaceRecord updatedPlace = PlaceRecord.builder()
        .placeId(2L)
        .country("Italy")
        .city("Rome")
        .name("Updated Colosseum")
        .tripIds(Set.of(1L))
        .images(List.of("updatedImage1.jpg"))
        .build();

    given()
        .port(port)
        .contentType("application/json")
        .body(updatedPlace)
        .when()
        .put(getBaseUri() + "/{id}", 2L)
        .then()
        .statusCode(200)
        .body("name", equalTo("Updated Colosseum")); // Confirm place name has been updated
  }

  @Test
  void testDeletePlace() {
    given()
        .port(port)
        .when()
        .delete(getBaseUri() + "/{id}", 3L)
        .then()
        .statusCode(204); // Check for no content on successful deletion
  }
}
