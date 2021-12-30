package com.letscode.resistance.service;

import com.letscode.resistance.dto.LocationDto;
import com.letscode.resistance.dto.RebelDto;
import com.letscode.resistance.entity.Location;
import com.letscode.resistance.entity.Rebel;
import com.letscode.resistance.enums.Genre;
import com.letscode.resistance.enums.RebelStatus;
import com.letscode.resistance.repository.LocationRepository;
import com.letscode.resistance.repository.RebelRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RebelService {

  private final RebelRepository rebelRepository;
  private final LocationRepository locationRepository;

  public Rebel findRebel(Long rebelId) {
    return rebelRepository
        .findById(rebelId)
        .orElseThrow(() -> new EntityNotFoundException("Rebel not found!"));
  }

  public Rebel updateLocation(Long rebelId, LocationDto locationDto) {
    Rebel rebel = findRebel(rebelId);

    Location location = new Location();
    location.setId(rebelId);
    location.setName(locationDto.getName());
    location.setLatitude(locationDto.getLatitude());
    location.setLongitude(locationDto.getLongitude());
    rebel.setLocation(location);

    return rebelRepository.save(rebel);
  }

  public Rebel reportTreason(Long rebelId, Long reporterId) {
    log.debug("Rebel {} reporting treason of {}.", rebelId, reporterId);
    Rebel rebel = findRebel(rebelId);
    rebel.addTreasonOccurrence(reporterId);

    if (rebel.getTreasonOccurrences().size() >= 3) {
      rebel.setStatus(RebelStatus.TRAITOR);
      log.warn("Rebel {} was marked as {}", rebel.getId(), RebelStatus.TRAITOR);
    }
    log.debug("Reporting treason executed.");
    return rebelRepository.save(rebel);
  }

  public Rebel createRebel(RebelDto rebelDto) {
    log.debug("Creating Rebel: {}.", rebelDto);
    Location location = new Location();
    location.setName(rebelDto.getLocation().getName());
    location.setLatitude(rebelDto.getLocation().getLatitude());
    location.setLongitude(rebelDto.getLocation().getLongitude());

    Rebel rebel =
        Rebel.builder()
            .name(rebelDto.getName())
            .age(rebelDto.getAge())
            .genre(Genre.valueOf(rebelDto.getGenre().toUpperCase()))
            .status(RebelStatus.ACTIVE)
            .inventory(rebelDto.getInventory())
            .location(location)
            .build();

    rebel.getLocation().setRebel(rebel);
    return rebelRepository.save(rebel);
  }
}
