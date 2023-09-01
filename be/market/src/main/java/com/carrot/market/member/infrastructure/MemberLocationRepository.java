package com.carrot.market.member.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carrot.market.member.domain.Member;
import com.carrot.market.member.domain.MemberLocation;

@Repository
public interface MemberLocationRepository extends JpaRepository<MemberLocation, Long> {
	@Query("select ml from MemberLocation ml "
		+ "join fetch ml.member "
		+ "join fetch ml.location "
		+ "where ml.member = :member")
	List<MemberLocation> findByMember(Member member);
}
