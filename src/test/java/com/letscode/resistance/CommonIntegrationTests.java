package com.letscode.resistance;

import com.letscode.resistance.dto.LocationDto;
import com.letscode.resistance.dto.RebelDto;
import com.letscode.resistance.dto.ReporterDto;
import com.letscode.resistance.enums.Resources;
import com.letscode.resistance.repository.RebelRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

public class CommonIntegrationTests {

  @LocalServerPort protected int port;
  @Autowired protected TestRestTemplate restTemplate;
  @Autowired protected RebelRepository rebelRepository;

  public static final String WEAPON = Resources.WEAPON.name();
  public static final String AMMUNITION = Resources.AMMUNITION.name();
  public static final String WATER = Resources.WATER.name();
  public static final String FOOD = Resources.FOOD.name();

  protected Long createRebelRequest(RebelDto rebelToCreate) {
    ResponseEntity<Map> response =
        restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/rebel/", rebelToCreate, Map.class);
    return Long.valueOf(response.getBody().get("id").toString());
  }

  protected void reportTreasonRequest(Long traitorId, Long reporterId) {
    ReporterDto reporterDto = new ReporterDto();
    reporterDto.setReporterId(reporterId);
    restTemplate.postForLocation(
        "http://localhost:" + port + "/api/v1/rebel/" + traitorId + "/report_treason", reporterDto);
  }

  protected RebelDto buildDefaultRebelDto() {
    RebelDto rebelToCreate = new RebelDto();
    rebelToCreate.setName("test");
    rebelToCreate.setAge(33);
    rebelToCreate.setGenre("WOMAN");

    LocationDto location = new LocationDto();
    location.setName("Galaxy Y");
    location.setLatitude(123L);
    location.setLongitude(456L);
    rebelToCreate.setLocation(location);

    rebelToCreate.setInventory(Map.of(WEAPON, 10, AMMUNITION, 10, WATER, 10, FOOD, 10));

    return rebelToCreate;
  }
}
