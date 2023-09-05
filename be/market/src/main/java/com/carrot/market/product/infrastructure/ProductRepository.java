package com.carrot.market.product.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carrot.market.product.application.dto.response.ProductSellerDetaillDto;
import com.carrot.market.product.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	@Query(value =
		"select new com.carrot.market.product.application.dto.response.ProductSellerDetaillDto( count(distinct chat) , count(distinct wish) , l.name , p.status , c.name , p.createdAt , p.productDetails.content , p.productDetails.hits , p.productDetails.name , p.productDetails.price , seller.id, seller.nickname) "
			+ " from Product  p "
			+ "join Chatroom as chat on chat.product= p "
			+ "join WishList as wish on wish.product = p "
			+ "join  p.seller as seller "
			+ "join  p.location as  l "
			+ "join  p.category as c "
			+ "group by p,c,l,seller"
	)
	ProductSellerDetaillDto findProductDetailbyId(Long productId);

	@Query("select p.viewCount from Product p where p.id = :id")
	Long findViewCount(@Param("id") Long id);

	@Modifying(clearAutomatically = true)
	@Query("update Product p set p.viewCount = :viewCnt where p.id = :id")
	void applyViewCntToRDB(@Param("id") Long id, @Param("viewCnt") Long viewCnt);
}
