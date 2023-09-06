package com.carrot.market.member.infrastructure;

import static com.carrot.market.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.carrot.market.image.infrastructure.ImageRepository;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.domain.WishList;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.infrastructure.ProductImageRepository;
import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.support.IntegrationTestSupport;

class WishListRepositoryTest extends IntegrationTestSupport {
	@Autowired
	ImageRepository imageRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	ProductImageRepository productImageRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	WishListRepository wishListRepository;

	@Test
	void isMemberLikeProduct() {
		// given
		Member june = makeMember("june", "sadfas");
		Member bean = makeMember("bean", "sadfas");
		memberRepository.saveAll(List.of(june, bean));
		Product product = Product.builder().seller(june).build();
		productRepository.save(product);

		WishList wishList = makeWishList(product, june);
		wishListRepository.save(wishList);

		Boolean isLiked = wishListRepository.existsWishListByMemberIdAndProductId(june.getId(), product.getId());
		assertThat(isLiked).isTrue();
		Boolean isLiked2 = wishListRepository.existsWishListByMemberIdAndProductId(bean.getId(), product.getId());
		assertThat(isLiked2).isFalse();
	}
}