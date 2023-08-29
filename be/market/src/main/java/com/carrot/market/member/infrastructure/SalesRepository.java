package com.carrot.market.member.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrot.market.member.domain.Sales;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {
}
