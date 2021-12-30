package com.letscode.resistance.controller;

import com.letscode.resistance.service.ReportService;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(value = "/api/v1/report")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  @ApiOperation(value = "Retrieves Traitors percentage")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/traitors_percentage")
  public Map<String, Double> getTraitorPercentage() {
    return reportService.getTraitorsPercentage();
  }

  @ApiOperation(value = "Retrieves Rebels percentage")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/rebels_percentage")
  public Map<String, Double> getRebelPercentage() {
    return reportService.getRebelsPercentage();
  }

  @ApiOperation(value = "Retrieves resource average per type.")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/resource_average")
  public Map<String, Double> getResourceAverage() {
    return reportService.getResourceAveragePerRebel();
  }

  @ApiOperation(value = "Retrieves resource points lost due Traitors.")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/lost_resources")
  public Map<String, Integer> getLostResourcePoints() {
    return reportService.getLostResourcePoints();
  }
}
