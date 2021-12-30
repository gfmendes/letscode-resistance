package com.letscode.starwarresistance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.letscode.starwarresistance.dto.TradeRequestDto;
import com.letscode.starwarresistance.dto.TraderDto;
import com.letscode.starwarresistance.entity.Location;
import com.letscode.starwarresistance.entity.Rebel;
import com.letscode.starwarresistance.repository.RebelRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class StarWarResistanceApplicationTests {

  @LocalServerPort private int port;
  @Autowired private TestRestTemplate restTemplate;
  @Autowired RebelRepository repository;

  @Test
  void whenCreatingNewRebelThenAssertBasicInfoAndLocation() {

    Rebel rebelToCreate = buildTestRebel();
    ResponseEntity<Rebel> rebelCreated = createRebel(rebelToCreate);

    Rebel rebel = repository.findById(rebelCreated.getBody().getId()).get();

    assertNotNull(rebel.getId());
    assertEquals(rebelToCreate.getName(), rebel.getName());
    assertEquals(rebelToCreate.getAge(), rebel.getAge());
    assertEquals(rebelToCreate.getGenre(), rebel.getGenre());

    assertNotNull(rebel.getLocation().getId());
    assertEquals(rebelToCreate.getLocation().getName(), rebel.getLocation().getName());
    assertEquals(rebelToCreate.getLocation().getLatitude(), rebel.getLocation().getLatitude());
    assertEquals(rebelToCreate.getLocation().getLongitude(), rebel.getLocation().getLongitude());

    assertNotNull(rebel.getInventory());
    assertEquals(3, rebel.getInventory().get("Weapon"));
    assertEquals(7, rebel.getInventory().get("Ammunition"));
    assertEquals(6, rebel.getInventory().get("Water"));
    assertEquals(4, rebel.getInventory().get("Food"));
  }

  private ResponseEntity<Rebel> createRebel(Rebel rebelToCreate) {
    return restTemplate.postForEntity(
        "http://localhost:" + port + "/api/v1/rebel/", rebelToCreate, Rebel.class);
  }

  @Test
  void whenSettingNewLocationThenUpdateRebelLocation() {

    Rebel rebelToCreate = buildTestRebel();
    ResponseEntity<Rebel> rebelCreated = createRebel(rebelToCreate);
    Location newLocation = new Location();
    newLocation.setName("Galaxy E");
    newLocation.setLatitude(999L);
    newLocation.setLongitude(555L);

    restTemplate.put(
        "http://localhost:"
            + port
            + "/api/v1/rebel/"
            + rebelCreated.getBody().getId()
            + "/location",
        newLocation,
        Rebel.class);

    Rebel rebel = repository.findById(rebelCreated.getBody().getId()).get();
    assertEquals(newLocation.getName(), rebel.getLocation().getName());
    assertEquals(newLocation.getLatitude(), rebel.getLocation().getLatitude());
    assertEquals(newLocation.getLongitude(), rebel.getLocation().getLongitude());
  }

  @Test
  void whenReportingTreason3xThenMarkRebelAsTraitor() {}

  @Test
  void whenReportingSameTraitorThenReturnError() {}

  @Test
  void whenValidTradeThenSwapItems() {
    createRebel(buildTestRebel());
    createRebel(buildTestRebel());

    TraderDto trader1 = new TraderDto();
    trader1.setRebelId(1L);
    trader1.setItems(Map.of("Weapon", 1, "Ammunition", 4, "Water", 9, "Food", 1));

    TraderDto trader2 = new TraderDto();
    trader2.setRebelId(2L);
    trader2.setItems(Map.of("Weapon", 10, "Ammunition", 0, "Food", 5));

    TradeRequestDto tradeRequestDto = new TradeRequestDto();
    tradeRequestDto.setTraders(List.of(trader1, trader2));

    ResponseEntity<Boolean> response =
        restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/rebel/trade_items",
            tradeRequestDto,
            Boolean.class);

    System.out.printf(repository.findById(1L).get().getInventory().toString());
    System.out.printf(repository.findById(2L).get().getInventory().toString());
  }

  private Rebel buildTestRebel() {
    Rebel rebelToCreate = new Rebel();
    rebelToCreate.setName("test");
    rebelToCreate.setAge(33);
    rebelToCreate.setGenre(1);

    Location location = new Location();
    location.setName("Galaxy Y");
    location.setLatitude(123L);
    location.setLongitude(456L);
    rebelToCreate.setLocation(location);

    rebelToCreate.setInventory(Map.of("Weapon", 10, "Ammunition", 10, "Water", 10, "Food", 10));

    return rebelToCreate;
  }
}
