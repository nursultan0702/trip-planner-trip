package com.tripplannertrip;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.MountableFile;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"spring.datasource.url=${spring.datasource.url}", "eureka.client.enabled=false"})
public class AbstractIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
      .withDatabaseName("trip_planner")
      .withCopyFileToContainer(MountableFile.forClasspathResource("schema.sql"),
          "/docker-entrypoint-initdb.d/");

  @DynamicPropertySource
  public static void setupPostgres(DynamicPropertyRegistry registry) {
    Startables.deepStart(postgres).join();
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);

    // Add logging here
    System.out.println("JDBC URL: " + postgres.getJdbcUrl());
    System.out.println("Username: " + postgres.getUsername());
    System.out.println("Password: " + postgres.getPassword());
  }

  @Test
  void isPostgresHealthy() {
    assertThat(postgres.isCreated()).isTrue();
    assertThat(postgres.isRunning()).isTrue();
  }
}
