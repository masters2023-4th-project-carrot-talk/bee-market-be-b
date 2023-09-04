package com.carrot.market.member.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carrot.market.member.domain.WishList;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
	@Query("select count(wl.id) > 0 from WishList as wl where wl.member.id = :member_id and wl.product.id = :product_id")
	Boolean existsMemberLikeProduct(@Param("member_id") Long memberId, @Param("product_id") Long productId);

}
