package com.carrot.market.member.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carrot.market.member.domain.Member;
import com.carrot.market.member.domain.WishList;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
	Boolean existsWishListByMemberIdAndProductId(@Param("member_id") Long memberId,
		@Param("product_id") Long productId);

	@Query("select wl from WishList wl "
		+ "join fetch wl.category c "
		+ "join fetch wl.product p")
	List<WishList> findWishListByMember(Member member);
}
