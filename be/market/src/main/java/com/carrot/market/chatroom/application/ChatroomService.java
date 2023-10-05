package com.carrot.market.chatroom.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chat.infrastructure.mongo.ChattingRepository;
import com.carrot.market.chatroom.application.dto.response.ChatroomInfo;
import com.carrot.market.chatroom.application.dto.response.ChattingAdditionalInfoResponse;
import com.carrot.market.chatroom.application.dto.response.ChattingListResponse;
import com.carrot.market.chatroom.application.dto.response.ChattingOpponentResponse;
import com.carrot.market.chatroom.application.dto.response.ChattingProductResponse;
import com.carrot.market.chatroom.application.dto.response.ChattingResponse;
import com.carrot.market.chatroom.application.dto.response.ChattingroomListResponse;
import com.carrot.market.chatroom.application.dto.response.UnreadChatTotalCountResponse;
import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.domain.ChatroomCounter;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.chatroom.infrastructure.dto.ChatroomResponse;
import com.carrot.market.chatroom.infrastructure.redis.ChatroomCounterRepository;
import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.ChatroomException;
import com.carrot.market.global.exception.domain.MemberException;
import com.carrot.market.global.exception.domain.ProductException;
import com.carrot.market.image.domain.Image;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.infrastructure.ProductImageRepository;
import com.carrot.market.product.infrastructure.ProductRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatroomService {
	private final int CHATTING_SIZE = 20;
	private final ChatroomRepository chatroomRepository;
	private final MemberRepository memberRepository;
	private final ProductRepository productRepository;
	private final ChattingRepository chattingRepository;
	private final ProductImageRepository productImageRepository;

	private final ChatroomCounterRepository chatRoomCounterRepository;

	@Transactional
	public Long getChatroomId(Long productId, Long purchaserId) {
		Chatroom chatroom = chatroomRepository.findByProductIdAndPurchaserId(productId,
			purchaserId).orElseGet(() -> makeChatroom(productId, purchaserId));
		return chatroom.getId();
	}

	public Chatroom makeChatroom(Long productId, Long purchaserId) {
		Product product = findByproductId(productId);
		Member purchaser = findByMemberId(purchaserId);

		Chatroom chatroom = new Chatroom(product, purchaser);
		return chatroomRepository.save(chatroom);
	}

	public List<ChattingroomListResponse> getChattingroomList(Long memberId) {
		List<ChatroomResponse> chattingList = chatroomRepository.findChatRoomsByMemberId(memberId);

		// 채팅방의 상세 정보를 가져옵니다.
		List<ChatroomInfo> chatDetails = chattingRepository.getChatDetails(
			chattingList.stream()
				.map(ChatroomResponse::getChatroomId)
				.collect(Collectors.toList()), memberId);

		// 채팅방 상세 정보를 Map 형태로 변환하여 쉽게 접근할 수 있게 합니다.
		Map<Long, ChatroomInfo> chatDetailsMap = chatDetails.stream()
			.collect(Collectors.toMap(ChatroomInfo::chatRoomId, Function.identity()));

		// 채팅방 리스트와 상세 정보를 결합하여 최종 응답을 생성합니다.
		return chattingList.stream()
			.filter(chatting -> chatDetailsMap.containsKey(chatting.getChatroomId()))
			.map(chatting -> new ChattingroomListResponse(chatting, chatDetailsMap.get(chatting.getChatroomId())))
			.collect(Collectors.toList());
	}

	public ChattingAdditionalInfoResponse getChattingAdditionalInfo(Long chatroomId, Long senderId) {
		Chatroom chatroom = findByChatroomId(chatroomId);
		Member opponent = getSenderOpponent(chatroom, senderId);
		Product product = chatroom.getProduct();

		ChattingOpponentResponse chattingOpponentResponse = ChattingOpponentResponse.from(opponent);
		Image thumbnail = productImageRepository.findMainImageIdByProduct(product.getId());
		ChattingProductResponse chattingProductResponse = ChattingProductResponse.from(product, thumbnail);

		return ChattingAdditionalInfoResponse.from(chattingOpponentResponse, chattingProductResponse);
	}

	private Member getSenderOpponent(Chatroom chatroom, Long senderId) {
		if (isSenderEqualPurchaser(senderId, chatroom)) {
			return chatroom.getProduct().getSeller();
		}
		return chatroom.getPurchaser();
	}

	private boolean isSenderEqualPurchaser(Long memberId, Chatroom chatroom) {
		return Objects.equals(chatroom.getPurchaser().getId(), memberId);
	}

	public ChattingResponse getChattingList(Long chatroomId, String chattingId) {
		List<Chatting> chattings = findByChatRoomIdWithPageable(chatroomId, chattingId);
		String chattingNextId = getChattingNextId(chattings, CHATTING_SIZE);
		chattings = removeLastIfChattingsSizeOverPageSize(chattings, CHATTING_SIZE);
		List<ChattingListResponse> chattingListResponses = chattings.stream().map(ChattingListResponse::new).toList();

		return new ChattingResponse(chattingListResponses, chattingNextId);
	}

	private List<Chatting> removeLastIfChattingsSizeOverPageSize(List<Chatting> chattings, int size) {
		if (chattings.size() == size + 1) {
			return popLast(chattings);
		}
		return chattings;
	}

	private List<Chatting> popLast(List<Chatting> chattings) {
		return chattings.subList(0, chattings.size() - 1);
	}

	private String getChattingNextId(List<Chatting> chattings, int size) {
		String nextChattingId = null;
		if (chattings != null && chattings.size() == size + 1) {
			nextChattingId = chattings.get(chattings.size() - 1).getId();
		}
		return nextChattingId;
	}

	private List<Chatting> findByChatRoomIdWithPageable(Long chatroomId, String chattingId) {
		LocalDateTime targetDateTime = getTargetDateTime(chattingId);
		return chattingRepository.findRecentChatsByChatRoomId(chatroomId, targetDateTime, CHATTING_SIZE + 1);
	}

	private LocalDateTime getTargetDateTime(String chattingId) {
		if (chattingId == null) {
			return LocalDateTime.now();
		}

		Optional<Chatting> chatting = chattingRepository.findById(chattingId);
		return chatting.map(Chatting::getCreatedAt).orElse(LocalDateTime.now());
	}

	private Chatroom findByChatroomId(Long chatroomId) {
		return chatroomRepository.findById(chatroomId)
			.orElseThrow(() -> new ApiException(ChatroomException.NOT_FOUND_CHATROOM));
	}

	private Member findByMemberId(Long purchaserId) {
		return memberRepository.findById(purchaserId)
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
	}

	private Product findByproductId(Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new ApiException(ProductException.NOT_FOUND_PRODUCT));
	}

	@Transactional
	public void connectChatRoom(Long chatRoomId, String sessionId) {
		ChatroomCounter chatRoomCounter = ChatroomCounter.builder().chatroomId(chatRoomId).sessionId(sessionId).build();
		chatRoomCounterRepository.save(chatRoomCounter);
	}

	public void disconnectChatRoom(String sessionId) {
		Optional<ChatroomCounter> chatRoomCounter = chatRoomCounterRepository.findBySessionId(sessionId);
		chatRoomCounter.ifPresent(roomCounter -> chatRoomCounterRepository.deleteById(roomCounter.getId()));
	}

	public UnreadChatTotalCountResponse getUnreadChatTotalCountInChatrooms(Long memberId) {
		Member member = findByMemberId(memberId);
		List<Long> chatroomIds = chatroomRepository.findChatRoomIdsByMember(member);
		return getUnreadChatCountForChatrooms(chatroomIds, memberId);
	}

	private UnreadChatTotalCountResponse getUnreadChatCountForChatrooms(List<Long> chatroomIds, Long memberId) {
		UnreadChatTotalCountResponse unreadChatTotalCount = chattingRepository.getUnreadChatTotalCount(chatroomIds,
			memberId);
		return unreadChatTotalCount != null ? unreadChatTotalCount : new UnreadChatTotalCountResponse(0L);
	}
}
