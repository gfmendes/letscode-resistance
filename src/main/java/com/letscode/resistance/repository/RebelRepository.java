package com.letscode.resistance.repository;

import com.letscode.resistance.entity.Rebel;
import com.letscode.resistance.enums.RebelStatus;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface RebelRepository extends CrudRepository<Rebel, Long> {

  Long countByStatus(RebelStatus isTraitor);

  List<Rebel> findByStatus(RebelStatus traitor);
}
