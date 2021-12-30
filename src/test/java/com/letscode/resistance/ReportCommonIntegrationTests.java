package com.letscode.resistance;

import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ReportCommonIntegrationTests extends CommonIntegrationTests {

  @BeforeEach
  void setup() {
    rebelRepository.deleteAll();
    //each rebel/traitor is 100 points worth in items
    Long traitor1Id = createRebelRequest(buildDefaultRebelDto());
    Long traitor2Id = createRebelRequest(buildDefaultRebelDto());
    Long reporter1Id = createRebelRequest(buildDefaultRebelDto());
    Long reporter2Id = createRebelRequest(buildDefaultRebelDto());
    Long reporter3Id = createRebelRequest(buildDefaultRebelDto());

    reportTreasonRequest(traitor1Id, reporter1Id);
    reportTreasonRequest(traitor1Id, reporter2Id);
    reportTreasonRequest(traitor1Id, reporter3Id);

    reportTreasonRequest(traitor2Id, reporter1Id);
    reportTreasonRequest(traitor2Id, reporter2Id);
    reportTreasonRequest(traitor2Id, reporter3Id);
  }

  @Test
  void whenGeneratingRebelsPercentageThenReturnsTheValue() {
    HttpEntity<Map> response =
        restTemplate.getForEntity(getReportBaseUrl() + "rebels_percentage", Map.class);
    Assertions.assertEquals(60D, response.getBody().get("rebels_percentage"));
  }

  @Test
  void whenGeneratingTraitorsPercentageThenReturnsTheValue() {
    HttpEntity<Map> response =
        restTemplate.getForEntity(getReportBaseUrl() + "traitors_percentage", Map.class);
    Assertions.assertEquals(40D, response.getBody().get("traitors_percentage"));
  }

  @Test
  void whenGeneratingResourceAverageThenReturnsTheValuePerResource() {
    HttpEntity<Map> response =
        restTemplate.getForEntity(getReportBaseUrl() + "resource_average", Map.class);
    Assertions.assertEquals(10D, response.getBody().get(WEAPON));
    Assertions.assertEquals(10D, response.getBody().get(AMMUNITION));
    Assertions.assertEquals(10D, response.getBody().get(WATER));
    Assertions.assertEquals(10D, response.getBody().get(FOOD));
  }

  @Test
  void whenGeneratingLostResourcesThenReturnsTheValue() {
    HttpEntity<Map> response =
        restTemplate.getForEntity(getReportBaseUrl() + "lost_resources", Map.class);
    Assertions.assertEquals(200, response.getBody().get("lost_resources_points"));
  }

  private String getReportBaseUrl() {
    return "http://localhost:" + port + "/api/v1/report/";
  }
}
