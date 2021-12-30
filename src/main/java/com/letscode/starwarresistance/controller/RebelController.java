package com.letscode.starwarresistance.controller;

import com.letscode.starwarresistance.dto.TradeRequestDto;
import com.letscode.starwarresistance.dto.TraderDto;
import com.letscode.starwarresistance.entity.Location;
import com.letscode.starwarresistance.entity.Rebel;
import com.letscode.starwarresistance.repository.LocationRepository;
import com.letscode.starwarresistance.repository.RebelRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(value = "/api/v1/rebel")
@RequiredArgsConstructor
@Slf4j
public class RebelController {
  private final RebelRepository rebelRepository;
  private final LocationRepository locationRepository;

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{rebelId}")
  public Rebel getRebel(@PathVariable Long rebelId) {
    return rebelRepository.findById(rebelId).orElse(null); // TODO:
  }

  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{rebelId}/location")
  public Rebel updateRebelLocation(@PathVariable Long rebelId, @RequestBody Location location) {
    Rebel rebel = rebelRepository.findById(rebelId).orElse(null); // TODO:
    location.setId(rebelId);
    rebel.setLocation(location);
    return rebelRepository.save(rebel);
  }

  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{rebelId}/report_treason")
  public Rebel reportRebelTreason(@PathVariable Long rebelId, @RequestBody Rebel reporter) {
    Rebel rebel = rebelRepository.findById(rebelId).orElse(null); // TODO:
    rebel.addTreasonOccurrence(reporter.getId());

    if (rebel.getTreasonOccurrences().size() >= 3) {
      rebel.setTraitor(Boolean.TRUE);
    }
    return rebelRepository.save(rebel);
  }

  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/")
  public Rebel createRebel(@RequestBody Rebel rebel) {
    rebel.getLocation().setRebel(rebel);
    return rebelRepository.save(rebel);
  }

  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/trade_items")
  public boolean tradeItems(@Valid @RequestBody TradeRequestDto tradeRequestDto) {

    validateTradeFairness(tradeRequestDto);

    TraderDto traderLeft = getTrader(tradeRequestDto, 0);
    TraderDto traderRight = getTrader(tradeRequestDto, 1);

    // TODO: Transaction
    executeExchange(traderLeft, traderRight.getItems());
    executeExchange(traderRight, traderLeft.getItems());

    return true;
  }

  private TraderDto getTrader(@RequestBody @Valid TradeRequestDto tradeRequestDto, int i) {
    return tradeRequestDto.getTraders().get(i);
  }

  private void validateTradeFairness(TradeRequestDto tradeRequestDto) {
    validateNumberOfTraders(tradeRequestDto);
    validateItems(tradeRequestDto);
    validatePoints(tradeRequestDto);
  }

  private void validateNumberOfTraders(TradeRequestDto tradeRequestDto) {
    if (tradeRequestDto.getTraders().size() != 2) {
      throw new IllegalArgumentException("Trading must have 2 traders.");
    }
  }

  private void validateItems(TradeRequestDto tradeRequestDto) {
    final Set<String> leftItems = getTrader(tradeRequestDto, 0).getItems().keySet();
    final Set<String> rightItems = getTrader(tradeRequestDto, 1).getItems().keySet();
    Optional<String> invalidItem =
        Stream.of(leftItems, rightItems)
            .flatMap(Collection::stream)
            .filter(RebelController::isNotValidItem)
            .findAny();

    if (invalidItem.isPresent()) {
      throw new IllegalArgumentException("item " + invalidItem.get() + " is invalid");
    }
  }

  private void validatePoints(TradeRequestDto tradeRequestDto) {
    final TraderDto traderLeft = getTrader(tradeRequestDto, 0);
    final TraderDto traderRight = getTrader(tradeRequestDto, 1);
    if (sumTraderPoints(traderLeft) - sumTraderPoints(traderRight) != 0) {
      throw new IllegalArgumentException("not a fair trade.");
    }
  }

  private int sumTraderPoints(TraderDto trader) {
    return trader.getItems().keySet().stream()
        .mapToInt(item -> trader.getItems().get(item) * getWeight(item))
        .sum();
  }

  private void executeExchange(TraderDto trader, Map<String, Integer> addedItems) {

    Rebel rebel = findAndValidateTrader(trader);
    Map<String, Integer> removedItems = trader.getItems();

    for (String item : List.of("Weapon", "Ammunition", "Water", "Food")) {
      Integer totalAfterSum =
          rebel.getInventory().getOrDefault(item, 0)
              - removedItems.getOrDefault(item, 0)
              + addedItems.getOrDefault(item, 0);

      rebel.getInventory().put(item, totalAfterSum);
    }
    rebelRepository.save(rebel);
  }

  private Rebel findAndValidateTrader(TraderDto trader) {
    Rebel rebel =
        rebelRepository
            .findById(trader.getRebelId())
            .orElseThrow(() -> new IllegalArgumentException("trader does not exists."));

    validateRebelStatus(rebel);
    validateRebelResources(trader, rebel);

    return rebel;
  }

  private void validateRebelStatus(Rebel rebel) {
    if (rebel.isTraitor()) {
      throw new IllegalArgumentException("trader " + rebel.getId() + " is a traitor.");
    }
  }

  private void validateRebelResources(TraderDto trader, Rebel rebel) {
    for (String item : trader.getItems().keySet()) {

      Integer remainingItems =
          rebel.getInventory().getOrDefault(item, 0) - trader.getItems().getOrDefault(item, 0);

      if (remainingItems < 0) {
        throw new IllegalArgumentException(
            "Rebel " + rebel.getId() + " doesn't have enough " + item + " to execute the trade.");
      }
    }
  }

  private Integer getWeight(String item) {
    return 1; //TODO:
  }

  private static boolean isNotValidItem(String item) {
    return !List.of("Weapon", "Ammunition", "Water", "Food").contains(item);
  }
}
