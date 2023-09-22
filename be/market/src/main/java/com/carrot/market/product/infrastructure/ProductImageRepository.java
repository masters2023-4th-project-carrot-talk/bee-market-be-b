package com.carrot.market.product.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carrot.market.image.domain.Image;
import com.carrot.market.product.domain.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

	@Query("select pi.image from ProductImage pi join pi.image where pi.product.id = :productId")
	List<Image> findImagesByProductId(@Param("productId") Long productId);

	@Query("select i from ProductImage pi join fetch Image i on pi.image = i where pi.product.id = :productId and pi.isMain = true ")
	Image findMainImageIdByProduct(@Param("productId") Long productId);
}
