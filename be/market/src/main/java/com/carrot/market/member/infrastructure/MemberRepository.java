package com.carrot.market.member.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carrot.market.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findBySocialId(String socialId);

	Optional<Member> findByNickname(String nickname);

	Optional<Member> findByRefreshToken(String refreshToken);

	@Modifying
	@Query("update Member m SET m.refreshToken = null where m.id = :memberId and m.refreshToken = :refreshToken")
	void updateRefreshTokenNullByUserIdAndRefreshToken(@Param("memberId") Long memberId,
		@Param("refreshToken") String refreshToken);

	@Modifying
	@Query("update Member m SET m.refreshToken = :refreshToken where m.id = :memberId")
	void updateRefreshTokenByUserIdAndRefreshToken(@Param("memberId") Long memberId,
		@Param("refreshToken") String refreshToken);
}
