package com.carrot.market.location.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrot.market.location.domain.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

	List<Location> findByNameContaining(String keyword);
}
