package com.carrot.market.image.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrot.market.image.domain.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
