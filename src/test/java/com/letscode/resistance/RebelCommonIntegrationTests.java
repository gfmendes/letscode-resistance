package com.letscode.resistance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.letscode.resistance.dto.RebelDto;
import com.letscode.resistance.entity.Location;
import com.letscode.resistance.entity.Rebel;
import com.letscode.resistance.enums.RebelStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RebelCommonIntegrationTests extends CommonIntegrationTests {

  @BeforeEach
  void setup() {
    rebelRepository.deleteAll();
  }

  @Test
  void whenCreatingNewRebelThenAssertBasicInfoAndLocation() {

    RebelDto rebelToCreate = buildDefaultRebelDto();
    Long rebelCreatedId = createRebelRequest(rebelToCreate);

    Rebel rebel = rebelRepository.findById(rebelCreatedId).get();

    assertNotNull(rebel.getId());
    assertEquals(rebelToCreate.getName(), rebel.getName());
    assertEquals(rebelToCreate.getAge(), rebel.getAge());
    assertEquals(rebelToCreate.getGenre(), rebel.getGenre().name());

    assertNotNull(rebel.getLocation().getId());
    assertEquals(rebelToCreate.getLocation().getName(), rebel.getLocation().getName());
    assertEquals(rebelToCreate.getLocation().getLatitude(), rebel.getLocation().getLatitude());
    assertEquals(rebelToCreate.getLocation().getLongitude(), rebel.getLocation().getLongitude());

    assertNotNull(rebel.getInventory());
    assertEquals(10, rebel.getInventory().get(WEAPON));
    assertEquals(10, rebel.getInventory().get(AMMUNITION));
    assertEquals(10, rebel.getInventory().get(WATER));
    assertEquals(10, rebel.getInventory().get(FOOD));
  }

  @Test
  void whenSettingNewLocationThenUpdateRebelLocation() {

    RebelDto rebelToCreate = buildDefaultRebelDto();
    Long rebelCreatedId = createRebelRequest(rebelToCreate);
    Location newLocation = new Location();
    newLocation.setName("Galaxy E");
    newLocation.setLatitude(999L);
    newLocation.setLongitude(555L);

    restTemplate.put(
        "http://localhost:" + port + "/api/v1/rebel/" + rebelCreatedId + "/location",
        newLocation,
        Rebel.class);

    Rebel rebel = rebelRepository.findById(rebelCreatedId).get();
    assertEquals(newLocation.getName(), rebel.getLocation().getName());
    assertEquals(newLocation.getLatitude(), rebel.getLocation().getLatitude());
    assertEquals(newLocation.getLongitude(), rebel.getLocation().getLongitude());
  }

  @Test
  void whenReportingTreason3xThenMarkRebelAsTraitor() {
    Long traitorCreatedId = createRebelRequest(buildDefaultRebelDto());
    Long rebel2CreatedId = createRebelRequest(buildDefaultRebelDto());
    Long rebel3CreatedId = createRebelRequest(buildDefaultRebelDto());
    Long rebel4CreatedId = createRebelRequest(buildDefaultRebelDto());

    reportTreasonRequest(traitorCreatedId, rebel2CreatedId);
    reportTreasonRequest(traitorCreatedId, rebel3CreatedId);
    reportTreasonRequest(traitorCreatedId, rebel4CreatedId);

    Rebel rebel = rebelRepository.findById(traitorCreatedId).get();
    assertEquals(RebelStatus.TRAITOR, rebel.getStatus());
  }

  @Test
  void whenSameReporterThenDoesNotMarkAsTraitor() {
    Long traitorCreatedId = createRebelRequest(buildDefaultRebelDto());
    Long rebel2CreatedId = createRebelRequest(buildDefaultRebelDto());
    Long rebel3CreatedId = createRebelRequest(buildDefaultRebelDto());

    reportTreasonRequest(traitorCreatedId, rebel2CreatedId);
    reportTreasonRequest(traitorCreatedId, rebel3CreatedId);
    reportTreasonRequest(traitorCreatedId, rebel3CreatedId); // Rebel3 reporting for the second time

    Rebel rebel = rebelRepository.findById(traitorCreatedId).get();
    assertEquals(RebelStatus.ACTIVE, rebel.getStatus());
  }

}
