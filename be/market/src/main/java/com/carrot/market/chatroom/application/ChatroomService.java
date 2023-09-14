package com.carrot.market.chatroom.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.chatroom.application.dto.response.ChatroomInfo;
import com.carrot.market.chatroom.application.dto.response.ChattingListResponse;
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
	private final String CHAT_ROOM_ID = "chatRoomId";
	private final String CHAT_READ_COUNT = "readCount";
	private final String CHAT_CREATED_AT = "createdAt";
	private final String CHAT_CONTENT = "content";
	private final String LATEST_CHAT_CONTENT = "latestChatContent";
	private final String UNREAD_CHAT_COUNT = "unreadChatCount";
	private final String CHATTING = "chatting";
	private final Long UNREAD = 1L;
	private final ChatroomRepository chatroomRepository;
	private final MemberRepository memberRepository;
	private final ProductRepository productRepository;
	private final MongoTemplate mongoTemplate;

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

	public List<ChattingListResponse> getChattingList(Long memberId) {
		List<ChatroomResponse> chattingList = chatroomRepository.getChattingByMemberId(
			memberId);
		List<ChatroomInfo> chatDetails = getChatDetails(
			chattingList.stream().map(ChatroomResponse::getChatroomId).toList());

		Map<Long, ChatroomInfo> chatDetailsMap = chatDetails.stream()
			.collect(Collectors.toMap(ChatroomInfo::chatRoomId, Function.identity()));

		return chattingList.stream()
			.map(chatting -> new ChattingListResponse(chatting, chatDetailsMap.get(chatting.getChatroomId())))
			.collect(Collectors.toList());
	}

	public List<ChatroomInfo> getChatDetails(List<Long> chatroomIds) {
		MatchOperation matchStage = Aggregation.match(
			Criteria.where(CHAT_ROOM_ID).in(chatroomIds).and(CHAT_READ_COUNT).is(UNREAD)
		);

		SortOperation sortStage = Aggregation.sort(Sort.Direction.DESC, CHAT_CREATED_AT);

		GroupOperation groupStage = Aggregation.group(CHAT_ROOM_ID)
			.last(CHAT_CONTENT).as(LATEST_CHAT_CONTENT)
			.last(CHAT_ROOM_ID).as(CHAT_ROOM_ID)
			.last(CHAT_CREATED_AT).as(CHAT_CREATED_AT)
			.count().as(UNREAD_CHAT_COUNT);

		Aggregation aggregation = Aggregation.newAggregation(matchStage, sortStage, groupStage);
		AggregationResults<ChatroomInfo> chatting = mongoTemplate.aggregate(aggregation, CHATTING,
			ChatroomInfo.class);
		return chatting.getMappedResults();
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
