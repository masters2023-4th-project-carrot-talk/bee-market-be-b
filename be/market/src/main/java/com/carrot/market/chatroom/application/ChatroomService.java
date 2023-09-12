package com.carrot.market.chatroom.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.MemberException;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.infrastructure.ProductRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatroomService {
	private final ChatroomRepository chatroomRepository;
	private final MemberRepository memberRepository;
	private final ProductRepository productRepository;

	public Long getChatroomId(Long productId, Long purchaserId) {
		return findByProductIdAndPurchaserId(productId, purchaserId).getId();
	}

	private Chatroom findByProductIdAndPurchaserId(Long productId, Long purchaserId) {
		return chatroomRepository.findByProductIdAndPurchaserId(productId, purchaserId).orElse(
			makeChatroom(productId, purchaserId)
		);
	}

	@Transactional
	public Chatroom makeChatroom(Long productId, Long purchaserId) {
		Product product = findByproductId(productId);
		Member purchaser = findBymemberId(purchaserId);

		Chatroom chatroom = new Chatroom(product, purchaser);
		return chatroomRepository.save(chatroom);
	}

	private Member findBymemberId(Long purchaserId) {
		return memberRepository.findById(purchaserId)
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
	}

	private Product findByproductId(Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
	}

}
