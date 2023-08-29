package com.carrot.market.product.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrot.market.product.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
