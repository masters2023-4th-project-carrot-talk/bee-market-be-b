package com.carrot.market.chatroom.infrastructure;

import static com.carrot.market.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.domain.SellingStatus;
import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.support.IntegrationTestSupport;

class ChatroomRepositoryTest extends IntegrationTestSupport {
	@Autowired
	ChatroomRepository chatroomRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	ProductRepository productRepository;

	@Test
	void findByProductIdAndPurchaserId() {
		// given
		Member purchaser = memberRepository.save(makeMember("june", "www.naver.com"));
		Product product = productRepository.save(makeProduct(purchaser, null, null, SellingStatus.SELLING, null));
		Chatroom chatroom = Chatroom.builder().purchaser(purchaser).product(product).build();
		chatroomRepository.save(chatroom);
		// when
		Optional<Chatroom> byProductIdAndPurchaserId = chatroomRepository.findByProductIdAndPurchaserId(product.getId(),
			purchaser.getId());

		// then
		assertAll(
			() -> assertThat(byProductIdAndPurchaserId).isPresent(),
			() -> assertThat(byProductIdAndPurchaserId.get().getId()).isEqualTo(chatroom.getId()),
			() -> assertThat(byProductIdAndPurchaserId.get().getProduct()).isEqualTo(product),
			() -> assertThat(byProductIdAndPurchaserId.get().getPurchaser()).isEqualTo(purchaser));
	}
}