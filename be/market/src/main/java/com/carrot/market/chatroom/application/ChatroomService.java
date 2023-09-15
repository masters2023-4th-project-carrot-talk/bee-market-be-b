package com.carrot.market.chatroom.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chat.infrastructure.mongo.ChattingRepository;
import com.carrot.market.chatroom.application.dto.response.ChatroomInfo;
import com.carrot.market.chatroom.application.dto.response.ChattingListResponse;
import com.carrot.market.chatroom.application.dto.response.ChattingroomListResponse;
import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.domain.ChatroomCounter;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.chatroom.infrastructure.dto.ChatroomResponse;
import com.carrot.market.chatroom.infrastructure.redis.ChatroomCounterRepository;
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
	private final ChattingRepository chattingRepository;

	private final ChatroomCounterRepository chatRoomCounterRepository;

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
		Member purchaser = findByMemberId(purchaserId);

		Chatroom chatroom = new Chatroom(product, purchaser);
		return chatroomRepository.save(chatroom);
	}

	public List<ChattingroomListResponse> getChattingroomList(Long memberId) {
		List<ChatroomResponse> chattingList = chatroomRepository.getChattingByMemberId(
			memberId);
		List<ChatroomInfo> chatDetails = chattingRepository.getChatDetails(
			chattingList.stream().map(ChatroomResponse::getChatroomId).toList());

		Map<Long, ChatroomInfo> chatDetailsMap = chatDetails.stream()
			.collect(Collectors.toMap(ChatroomInfo::chatRoomId, Function.identity()));

		return chattingList.stream()
			.map(chatting -> new ChattingroomListResponse(chatting, chatDetailsMap.get(chatting.getChatroomId())))
			.toList();
	}

	public List<ChattingListResponse> getChattingList(Long chatroomId, String chattingId) {
		List<Chatting> chattings = findByChatRoomIdWithPageable(chatroomId, chattingId);

		return chattings.stream().map(ChattingListResponse::new).toList();
	}

	private List<Chatting> findByChatRoomIdWithPageable(Long chatroomId, String chattingId) {
		Optional<Chatting> chatting = chattingRepository.findById(chattingId);
		if (chatting.isPresent()) {
			return chattingRepository.findByChatRoomIdWithPageable(chatroomId, chatting.get().getCreatedAt(), 10);

		}
		return chattingRepository.findByChatRoomIdWithPageable(chatroomId, LocalDateTime.now(), 10);
	}

	private Member findByMemberId(Long purchaserId) {
		return memberRepository.findById(purchaserId)
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
	}

	private Product findByproductId(Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
	}

	public void connectChatRoom(Long chatRoomId, Long senderId) {
		ChatroomCounter chatRoomCounter = ChatroomCounter.builder().chatroomId(chatRoomId).memberId(senderId).build();
		chatRoomCounterRepository.save(chatRoomCounter);
	}

	public void disconnectChatRoom(Long chatRoomId, Long memberId) {
		Optional<ChatroomCounter> chatRoomCounter = chatRoomCounterRepository.findByChatroomIdAndMemberId(
			chatRoomId, memberId);
		chatRoomCounter.ifPresent(roomCounter -> chatRoomCounterRepository.deleteById(roomCounter.getId()));
	}
}
