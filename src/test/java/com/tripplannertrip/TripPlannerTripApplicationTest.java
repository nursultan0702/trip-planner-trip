package com.tripplannertrip;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class TripPlannerTripApplicationTest extends AbstractIntegrationTest {



  protected RequestSpecification requestSpecification;

  @LocalServerPort
  protected int localServerPort;

  @BeforeEach
  void setUpAbstractIntegrationTest() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    requestSpecification = new RequestSpecBuilder()
        .setPort(localServerPort)
        .addHeader(
            HttpHeaders.CONTENT_TYPE,
            MediaType.APPLICATION_JSON_VALUE
        )
        .build();
  }

  @Test
  void healthy() {
    given(requestSpecification)
        .when()
        .get("/actuator/health")
        .then()
        .statusCode(200)
        .log().ifValidationFails(LogDetail.ALL);
  }
}