package com.carrot.market.chatroom.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrot.market.chatroom.domain.Chatroom;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
	Optional<Chatroom> findByProductIdAndPurchaserId(Long productId, Long purchaserId);
}
