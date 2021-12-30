package com.letscode.resistance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.letscode.resistance.dto.TradeRequestDto;
import com.letscode.resistance.dto.TraderDto;
import com.letscode.resistance.entity.Rebel;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TradeCommonIntegrationTests extends CommonIntegrationTests {

  @BeforeEach
  void setup() {
    rebelRepository.deleteAll();
  }

  @Test
  void whenTradeIsNotFairThenReturnBadRequest() {

    TraderDto traderLeft = buildTrader(Map.of(WEAPON, 1, AMMUNITION, 4, WATER, 9, FOOD, 1));
    TraderDto traderRight = buildTrader(Map.of(WEAPON, 11, AMMUNITION, 0, FOOD, 5));

    TradeRequestDto tradeRequestDto = new TradeRequestDto();
    tradeRequestDto.setTraders(List.of(traderLeft, traderRight));

    HttpEntity<Map> response =
        restTemplate.postForEntity(getTradeUrl(), tradeRequestDto, Map.class);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().get("status"));
    assertEquals(
        "Not a fair trade! Sum left trader = [35]. Sum right trader = [49].",
        response.getBody().get("message"));
  }

  @Test
  void whenItemDoesNotExistsThenReturnBadRequest() {

    TraderDto traderLeft = buildTrader(Map.of("JUJUBA", 1, AMMUNITION, 4, WATER, 9, FOOD, 1));
    TraderDto traderRight = buildTrader(Map.of(WEAPON, 11, AMMUNITION, 0, FOOD, 5));

    TradeRequestDto tradeRequestDto = new TradeRequestDto();
    tradeRequestDto.setTraders(List.of(traderLeft, traderRight));

    HttpEntity<Map> response =
        restTemplate.postForEntity(getTradeUrl(), tradeRequestDto, Map.class);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().get("status"));
    assertEquals("Item JUJUBA is invalid!", response.getBody().get("message"));
  }

  @Test
  void whenRebelDoesNotHaveEnoughThenReturnBadRequest() {

    TraderDto traderLeft = buildTrader(Map.of(FOOD, 20));
    TraderDto traderRight = buildTrader(Map.of(WEAPON, 5));

    TradeRequestDto tradeRequestDto = new TradeRequestDto();
    tradeRequestDto.setTraders(List.of(traderLeft, traderRight));

    HttpEntity<Map> response =
        restTemplate.postForEntity(getTradeUrl(), tradeRequestDto, Map.class);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().get("status"));
    assertEquals(
        "Rebel " + traderLeft.getRebelId() + " doesn't have enough FOOD to execute the trade!",
        response.getBody().get("message"));
  }

  @Test
  void whenRebelIsATraitorThenReturnBadRequest() {

    TraderDto traderLeft = buildTrader(Map.of(FOOD, 20));
    TraderDto traderRight = buildTrader(Map.of(WEAPON, 5));

    // Creating traitor reporters
    Long reporter1Id = createRebelRequest(buildDefaultRebelDto());
    Long reporter2Id = createRebelRequest(buildDefaultRebelDto());
    Long reporter3Id = createRebelRequest(buildDefaultRebelDto());
    // Reporting trader left
    reportTreasonRequest(traderLeft.getRebelId(), reporter1Id);
    reportTreasonRequest(traderLeft.getRebelId(), reporter2Id);
    reportTreasonRequest(traderLeft.getRebelId(), reporter3Id);

    TradeRequestDto tradeRequestDto = new TradeRequestDto();
    tradeRequestDto.setTraders(List.of(traderLeft, traderRight));

    HttpEntity<Map> response =
        restTemplate.postForEntity(getTradeUrl(), tradeRequestDto, Map.class);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().get("status"));
    assertEquals(
        "Trader " + traderLeft.getRebelId() + " is a traitor!", response.getBody().get("message"));
  }

  @Test
  void whenRebelDoesNotExistsThenReturnBadRequest() {

    TraderDto traderLeft = buildTrader(Map.of(WEAPON, 1, AMMUNITION, 4, WATER, 9, FOOD, 1));
    TraderDto traderRight = new TraderDto();
    traderRight.setRebelId(9999L);
    traderRight.setItems(Map.of(WEAPON, 1, AMMUNITION, 4, WATER, 9, FOOD, 1));

    TradeRequestDto tradeRequestDto = new TradeRequestDto();
    tradeRequestDto.setTraders(List.of(traderLeft, traderRight));

    HttpEntity<Map> response =
        restTemplate.postForEntity(getTradeUrl(), tradeRequestDto, Map.class);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().get("status"));
    assertEquals("Rebel does not exists!", response.getBody().get("message"));
  }

  @Test
  void whenTradeIsFairThenExchangeItems() {

    TraderDto traderLeft = buildTrader(Map.of(WEAPON, 5, WATER, 4)); // 20 + 8
    TraderDto traderRight = buildTrader(Map.of(AMMUNITION, 7, FOOD, 7)); // 21 + 7

    TradeRequestDto tradeRequestDto = new TradeRequestDto();
    tradeRequestDto.setTraders(List.of(traderLeft, traderRight));

    restTemplate.postForLocation(getTradeUrl(), tradeRequestDto);

    Rebel rebelLeft = rebelRepository.findById(traderLeft.getRebelId()).get();
    assertEquals(5, rebelLeft.getInventory().get(WEAPON)); // 10 - 5
    assertEquals(17, rebelLeft.getInventory().get(AMMUNITION)); // 10 + 7
    assertEquals(6, rebelLeft.getInventory().get(WATER)); // 10 - 4
    assertEquals(17, rebelLeft.getInventory().get(FOOD)); // 10 + 7

    Rebel rebelRight = rebelRepository.findById(traderRight.getRebelId()).get();
    assertEquals(15, rebelRight.getInventory().get(WEAPON)); // 10 + 5
    assertEquals(3, rebelRight.getInventory().get(AMMUNITION)); // 10 - 7
    assertEquals(14, rebelRight.getInventory().get(WATER)); // 10 + 4
    assertEquals(3, rebelRight.getInventory().get(FOOD)); // 10 - 7
  }

  private String getTradeUrl() {
    return "http://localhost:" + port + "/api/v1/rebel/trade_items";
  }

  private TraderDto buildTrader(Map items) {
    Long traderId = createRebelRequest(buildDefaultRebelDto());
    TraderDto trader = new TraderDto();
    trader.setRebelId(traderId);
    trader.setItems(items);
    return trader;
  }
}
