package com.letscode.starwarresistance.controller;

import com.letscode.starwarresistance.repository.LocationRepository;
import com.letscode.starwarresistance.repository.RebelRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/report")
public record ReportController(
    RebelRepository rebelRepository,
    LocationRepository locationRepository) {


}
