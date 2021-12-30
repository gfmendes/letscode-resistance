package com.letscode.resistance.service;

import com.letscode.resistance.entity.Rebel;
import com.letscode.resistance.enums.RebelStatus;
import com.letscode.resistance.enums.Resources;
import com.letscode.resistance.repository.RebelRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

  private final RebelRepository rebelRepository;

  public Map<String, Double> getTraitorsPercentage() {
    log.info("Generating Traitors Percentage report");
    Double totalRebels = Double.valueOf(rebelRepository.count());
    Double totalTraitors = Double.valueOf(rebelRepository.countByStatus(RebelStatus.TRAITOR));
    Double percentage = (totalTraitors / totalRebels) * 100;
    return Map.of("traitors_percentage", percentage);
  }

  public Map<String, Double> getRebelsPercentage() {
    log.info("Generating Rebels Percentage report");
    Double totalRebels = Double.valueOf(rebelRepository.count());
    Double totalActiveRebels = Double.valueOf(rebelRepository.countByStatus(RebelStatus.ACTIVE));
    Double percentage = (totalActiveRebels / totalRebels) * 100;
    return Map.of("rebels_percentage", percentage);
  }

  public Map<String, Double> getResourceAveragePerRebel() {
    log.info("Generating Average Resource Per Rebel report");
    List<Map<String, Integer>> inventoryList =
        rebelRepository.findByStatus(RebelStatus.ACTIVE).stream().map(Rebel::getInventory).toList();

    Map<String, Double> totalItems = calculateTotalItemsPerResource(inventoryList);
    return calculateAverage(totalItems, inventoryList.size());
  }

  private Map<String, Double> calculateTotalItemsPerResource(
      List<Map<String, Integer>> inventoryList) {
    Map<String, Double> items = new HashMap<>();
    for (Map<String, Integer> inventory : inventoryList) {
      for (Resources resource : Resources.values()) {
        items.put(
            resource.name(),
            inventory.getOrDefault(resource.name(), 0) + items.getOrDefault(resource.name(), 0D));
      }
    }
    return items;
  }

  private Map<String, Double> calculateAverage(Map<String, Double> totalItems, int totalRebels) {
    Map<String, Double> itemsAverage = new HashMap<>();
    for (Entry<String, Double> entry : totalItems.entrySet()) {
      Double average = entry.getValue() / totalRebels;
      itemsAverage.put(entry.getKey(), average);
    }
    return itemsAverage;
  }

  public Map<String, Integer> getLostResourcePoints() {
    log.info("Generating Lost Resources report");
    List<Rebel> traitorList = rebelRepository.findByStatus(RebelStatus.TRAITOR);
    Integer total =
        traitorList.stream().map(Rebel::getInventory).mapToInt(this::calculateTraitorPoints).sum();
    return Map.of("lost_resources_points", total);
  }

  private Integer calculateTraitorPoints(Map<String, Integer> inventory) {
    return inventory.keySet().stream()
        .mapToInt(k -> inventory.get(k) * Resources.valueOf(k.toUpperCase()).weight)
        .sum();
  }
}
