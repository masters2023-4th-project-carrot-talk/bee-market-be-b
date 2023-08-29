package com.carrot.market.product.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrot.market.product.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
