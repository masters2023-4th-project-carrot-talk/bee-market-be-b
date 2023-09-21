package com.carrot.market.product.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carrot.market.product.application.dto.response.ProductSellerDetailDto;
import com.carrot.market.product.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	@Query(value =
		"select new com.carrot.market.product.application.dto.response.ProductSellerDetailDto( count(distinct chat) , count(distinct wish) ,l.id, l.name , p.status , c.name , p.createdAt , p.productDetails.content , p.productDetails.hits , p.productDetails.title , p.productDetails.price , seller.id, seller.nickname) "
			+ "from Product  p "
			+ "join Chatroom as chat on chat.product= p "
			+ "join WishList as wish on wish.product = p "
			+ "join  p.seller as seller "
			+ "join  p.location as  l "
			+ "join  p.category as c "
			+ "where p.id = :productId "
			+ "group by p,c,l,seller"
	)
	ProductSellerDetailDto findProductDetailById(@Param("productId") Long productId);

	@Query("select p.productDetails.hits from Product p where p.id = :id")
	Long findHitsById(@Param("id") Long id);

	@Modifying(clearAutomatically = true)
	@Query("update Product p set p.productDetails.hits = p.productDetails.hits + :hits "
		+ "where p.id = :id")
	void applyHitsToRDB(@Param("id") Long id, @Param("hits") Long hits);
}
