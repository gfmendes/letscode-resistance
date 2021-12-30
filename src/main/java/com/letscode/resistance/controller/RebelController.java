package com.letscode.resistance.controller;

import com.letscode.resistance.dto.LocationDto;
import com.letscode.resistance.dto.RebelDto;
import com.letscode.resistance.dto.ReporterDto;
import com.letscode.resistance.dto.TradeRequestDto;
import com.letscode.resistance.entity.Rebel;
import com.letscode.resistance.service.RebelService;
import com.letscode.resistance.service.TradeService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@Validated
@RequestMapping(value = "/api/v1/rebel")
@RequiredArgsConstructor
public class RebelController {

  private final RebelService rebelService;
  private final TradeService tradeService;

  @ApiOperation(value = "Get Rebel by its Id. If Rebel is not found then returns 404.")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{rebelId}")
  public Rebel getRebel(@PathVariable Long rebelId) {
    try {
      return rebelService.findRebel(rebelId);
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @ApiOperation(
      value =
          "Updates Rebel Location and returns the updated Rebel."
              + " If Rebel is not found then returns 404.")
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{rebelId}/location")
  public Rebel updateRebelLocation(
      @PathVariable Long rebelId, @RequestBody LocationDto locationDto) {
    try {
      return rebelService.updateLocation(rebelId, locationDto);
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @ApiOperation(
      value =
          "Reports a Rebel treason. Traitor Id is passed as path variable. "
              + "Reporter Id is passed in the body. "
              + "A Reporter cannot report same traitor more than once. "
              + "If any Rebel is not found then returns 404.")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{rebelId}/report_treason")
  public Rebel reportRebelTreason(
      @PathVariable Long rebelId, @RequestBody ReporterDto reporterDto) {
    try {
      return rebelService.reportTreason(rebelId, reporterDto.getReporterId());
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @ApiOperation(
      value =
          "Creates a Rebel with basic data, Location and Inventory."
              + "Inventory is a Map<String, Integer> where keys should be, in uppercase, WEAPON, AMMUNITION, WATER AND FOOD. "
              + "The value represents the amount of items for that key.")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/")
  public Rebel createRebel(@Valid @RequestBody RebelDto rebelDto) {
    try {
      return rebelService.createRebel(rebelDto);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @ApiOperation(
      value =
          "Executes a trade between 2 Rebels."
              + "Items is a Map<String, Integer> where keys should be, in uppercase, WEAPON, AMMUNITION, WATER AND FOOD."
              + "The value represents the amount of items for that key. "
              + "This service validates if the trade is fair before execute the exchange.")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/trade_items")
  public List<Rebel> tradeItems(@Valid @RequestBody TradeRequestDto tradeRequestDto) {
    try {
      return tradeService.executeTrade(tradeRequestDto);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
