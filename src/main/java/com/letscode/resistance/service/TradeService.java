package com.letscode.resistance.service;

import com.letscode.resistance.dto.TradeRequestDto;
import com.letscode.resistance.dto.TraderDto;
import com.letscode.resistance.entity.Rebel;
import com.letscode.resistance.enums.Resources;
import com.letscode.resistance.repository.RebelRepository;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeService {

  private final RebelRepository rebelRepository;

  @Transactional
  public List<Rebel> executeTrade(TradeRequestDto tradeRequestDto) {
    log.debug("Executing trade []", tradeRequestDto);
    validateTradeFairness(tradeRequestDto);

    TraderDto traderLeft = getTrader(tradeRequestDto, 0);
    TraderDto traderRight = getTrader(tradeRequestDto, 1);

    Rebel rebelLeft = executeExchange(traderLeft, traderRight.getItems());
    Rebel rebelRight = executeExchange(traderRight, traderLeft.getItems());
    log.debug("Trade executed.");
    return List.of(rebelLeft, rebelRight);
  }

  private TraderDto getTrader(TradeRequestDto tradeRequestDto, int i) {
    return tradeRequestDto.getTraders().get(i);
  }

  private void validateTradeFairness(TradeRequestDto tradeRequestDto) {
    validateNumberOfTraders(tradeRequestDto);
    validateResources(tradeRequestDto);
    validatePoints(tradeRequestDto);
  }

  private void validateNumberOfTraders(TradeRequestDto tradeRequestDto) {
    if (tradeRequestDto.getTraders().size() != 2) {
      throw new IllegalArgumentException("Trading must have 2 traders.");
    }
  }

  private void validateResources(TradeRequestDto tradeRequestDto) {
    final Set<String> leftItems = getTrader(tradeRequestDto, 0).getItems().keySet();
    final Set<String> rightItems = getTrader(tradeRequestDto, 1).getItems().keySet();
    Optional<String> invalidItem =
        Stream.of(leftItems, rightItems)
            .flatMap(Collection::stream)
            .filter(Resources::isInvalidValidResource)
            .findAny();

    if (invalidItem.isPresent()) {
      throw new IllegalArgumentException("Item " + invalidItem.get() + " is invalid!");
    }
  }

  private void validatePoints(TradeRequestDto tradeRequestDto) {
    final int sumLeft = sumTraderPoints(getTrader(tradeRequestDto, 0));
    final int sumRight = sumTraderPoints(getTrader(tradeRequestDto, 1));
    if (sumLeft - sumRight != 0) {
      throw new IllegalArgumentException(
          String.format(
              "Not a fair trade! Sum left trader = [%s]. Sum right trader = [%s].",
              sumLeft, sumRight));
    }
  }

  private int sumTraderPoints(TraderDto trader) {
    return trader.getItems().keySet().stream()
        .mapToInt(item -> trader.getItems().get(item) * getResourceWeight(item))
        .sum();
  }

  private Rebel executeExchange(TraderDto trader, Map<String, Integer> itemsToAdd) {

    Rebel rebel = findAndValidateRebel(trader);
    Map<String, Integer> itemsToRemove = trader.getItems();

    for (String item : Resources.getValidResources()) {
      Integer totalAfterSum =
          rebel.getInventory().getOrDefault(item, 0)
              - itemsToRemove.getOrDefault(item, 0)
              + itemsToAdd.getOrDefault(item, 0);

      rebel.getInventory().put(item, totalAfterSum);
    }
    return rebelRepository.save(rebel);
  }

  private Rebel findAndValidateRebel(TraderDto trader) {
    Rebel rebel =
        rebelRepository
            .findById(trader.getRebelId())
            .orElseThrow(() -> new IllegalArgumentException("Rebel does not exists!"));

    validateRebelStatus(rebel);
    validateRebelResources(trader, rebel);

    return rebel;
  }

  private void validateRebelStatus(Rebel rebel) {
    if (rebel.isTraitor()) {
      throw new IllegalArgumentException("Trader " + rebel.getId() + " is a traitor!");
    }
  }

  private void validateRebelResources(TraderDto trader, Rebel rebel) {
    Map<String, Integer> inventory = rebel.getInventory();
    if (ObjectUtils.isEmpty(inventory)) {
      throw new IllegalArgumentException("Rebel " + rebel.getId() + " has empty inventory!");
    }

    for (String item : trader.getItems().keySet()) {
      Integer remainingItems =
          inventory.getOrDefault(item, 0) - trader.getItems().getOrDefault(item, 0);
      if (remainingItems < 0) {
        throw new IllegalArgumentException(
            "Rebel " + rebel.getId() + " doesn't have enough " + item + " to execute the trade!");
      }
    }
  }

  private Integer getResourceWeight(String item) {
    return Resources.valueOf(item.toUpperCase(Locale.ROOT)).weight;
  }
}
