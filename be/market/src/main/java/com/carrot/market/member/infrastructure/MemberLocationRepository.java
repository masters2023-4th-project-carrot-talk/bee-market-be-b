package com.carrot.market.member.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrot.market.member.domain.MemberLocation;

@Repository
public interface MemberLocationRepository extends JpaRepository<MemberLocation, Long> {
}
