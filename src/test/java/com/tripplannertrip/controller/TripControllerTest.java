package com.tripplannertrip.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tripplannertrip.AbstractIntegrationTest;
import com.tripplannertrip.model.TripRecord;
import com.tripplannertrip.service.TripService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;

class TripControllerTest extends AbstractIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TripService tripService;

  // Utility to get the base URI for tests
  private String getBaseUri() {
    return "http://localhost:" + port + "/v1/api/trips";
  }

  @Test
  void testGetTripById() {
    TripRecord tripRecord = given()
        .port(port)
        .when()
        .get(getBaseUri() + "/{id}", 1)
        .then()
        .statusCode(200)
        .extract()
        .as(TripRecord.class);
    assertEquals(1, tripRecord.tripId());
  }

  @Test
  @WithMockUser(username="testUser")
  void testCreateTrip() {
    TripRecord newTrip = TripRecord.builder()
        .name("Discovering New Places")
        .description("A wonderful journey.").build();

    TripRecord createdTrip = given()
        .port(port)
        .contentType("application/json")
        .body(newTrip)
        .when()
        .post(getBaseUri())
        .then()
        .statusCode(201)
        .extract()
        .as(TripRecord.class);

    assertNotNull(createdTrip);
    assertEquals("Discovering New Places", createdTrip.name());
  }

  @Test
  void testUpdateTrip() {
    long tripIdToUpdate = 1;
    TripRecord updateInfo = TripRecord.builder()
        .name("Updated Name")
        .description("Updated description.")
        .build();

    TripRecord updatedTrip = given()
        .port(port)
        .contentType("application/json")
        .body(updateInfo)
        .when()
        .put(getBaseUri() + "/{id}", tripIdToUpdate)
        .then()
        .statusCode(200)
        .extract()
        .as(TripRecord.class);

    assertEquals(tripIdToUpdate, updatedTrip.tripId());
    assertEquals("Updated Name", updatedTrip.name());
  }

  @Test
  void testDeleteTrip() {
    long tripIdToDelete = 2;

    given()
        .port(port)
        .when()
        .delete(getBaseUri() + "/{id}", tripIdToDelete)
        .then()
        .statusCode(204);

    given()
        .port(port)
        .when()
        .get(getBaseUri() + "/{id}", tripIdToDelete)
        .then()
        .statusCode(404);
  }


}