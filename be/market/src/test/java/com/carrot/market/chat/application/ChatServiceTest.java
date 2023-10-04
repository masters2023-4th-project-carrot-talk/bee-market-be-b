package com.carrot.market.chat.application;

import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chat.infrastructure.mongo.ChattingRepository;
import com.carrot.market.chat.presentation.dto.Entry;
import com.carrot.market.chat.presentation.dto.Message;
import com.carrot.market.chatroom.application.ChatroomService;
import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.domain.ChatroomCounter;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.chatroom.infrastructure.redis.ChatroomCounterRepository;
import com.carrot.market.fixture.chatting.RoomContext;
import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.ChattingException;
import com.carrot.market.jwt.application.JwtProvider;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.carrot.market.fixture.ChattingFixtureFactory.enterRoom;
import static com.carrot.market.fixture.FixtureFactory.makeMember;
import static com.carrot.market.global.filter.JwtAuthorizationFilter.MEMBER_ID;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

class ChatServiceTest extends IntegrationTestSupport {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ChatroomRepository chatroomRepository;
    @Autowired
    private ChatService chatService;
    @SpyBean
    private ChatroomService chatRoomService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private ChattingRepository chattingRepository;
    @Autowired
    private ChatroomCounterRepository chatRoomCounterRepository;

    private BlockingQueue<Message> blockingQueueForChatting;
    private BlockingQueue<Entry> blockingQueueForEntry;

    private RoomContext roomContext;

    private Member seller;
    private Member purchaser;
    private Product product;
    private Chatroom chatroom;
    private String accessToken;

    @BeforeEach
    void before() {
        TransactionSynchronizationManager.initSynchronization();
        blockingQueueForChatting = new LinkedBlockingDeque<>();
        blockingQueueForEntry = new LinkedBlockingDeque<>();
        roomContext = new RoomContext(blockingQueueForChatting, blockingQueueForEntry, port);

        seller = memberRepository.save(makeMember("June", "www.naver.com"));
        purchaser = memberRepository.save(makeMember("bean", "www.google.com"));
        product = productRepository.save(Product.builder().seller(seller).build());
        chatroom = chatroomRepository.save(new Chatroom(product, seller));
        accessToken = jwtProvider.createAccessToken(Map.of(MEMBER_ID, seller.getId()));
    }

    @AfterEach
    void after() {
        TransactionSynchronizationManager.clear();
        chattingRepository.deleteAll();
        chatRoomCounterRepository.deleteAll();
    }

    @Test
    void readChattingInChatroom() {
        // given
        Chatting chatting = Chatting.builder()
                .chatRoomId(chatroom.getId())
                .senderId(purchaser.getId())
                .content("content")
                .build();
        Chatting savedChatting = chattingRepository.save(chatting);

        // when
        chatService.readChattingInChatroom(chatroom.getId(), seller.getId());

        // then
        Chatting readChatting = chattingRepository.findById(savedChatting.getId()).orElseThrow(() -> new ApiException(
                ChattingException.INVALID_CHATTING_ID));
        assertAll(
                () -> assertThat(readChatting.getId()).isEqualTo(savedChatting.getId()),
                () -> assertThat(readChatting.isRead()).isEqualTo(true));
    }

    @Test
    void connectStompThenMakeChatRoom() throws ExecutionException, InterruptedException, TimeoutException {
        String content = "맥북 프로 사주세요";
        Message expectedMessageResponse = Message.builder()
                .senderId(purchaser.getId())
                .chatroomId(chatroom.getId())
                .content(content)
                .build();

        // init setting
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // Connection
        enterRoom(chatroom.getId(), accessToken, roomContext);
        sendMessage(purchaser.getId(), chatroom.getId(), content);
        blockingQueueForChatting.poll(5, SECONDS);
        Message message = blockingQueueForChatting.poll(5, SECONDS);

        //then
        List<Chatting> all = chattingRepository.findAll();
        assertThat(all).hasSize(1);

    }

    @Test
    void disconnectStomp() throws ExecutionException, InterruptedException, TimeoutException {

        // init setting
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // Connection
        StompSession session = enterRoom(chatroom.getId(), accessToken, roomContext);
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", accessToken);
        stompHeaders.add("chatRoomId", String.valueOf(chatroom.getId()));
        session.disconnect(stompHeaders);

        //then
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> verify(chatRoomService, atLeast(1)).disconnectChatRoom(anyString()));

        List<ChatroomCounter> byChatroomId = chatRoomCounterRepository.findByChatroomId(chatroom.getId());
        assertThat(byChatroomId).hasSize(0);

    }

    private void sendMessage(Long senderId, Long roomId, String content) {
        Message message = Message.builder().senderId(senderId).chatroomId(roomId).content(content).build();
        chatService.sendMessage(message);
    }

}