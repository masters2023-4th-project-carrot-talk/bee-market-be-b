package com.carrot.market.chatroom.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.infrastructure.dto.ChatroomResponse;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
	Optional<Chatroom> findByProductIdAndPurchaserId(Long productId, Long purchaserId);

	@Query(
		value =
			"SELECT\n"
				+ "    (CASE \n"
				+ "        WHEN seller.id = :memberId \n"
				+ "	       THEN purchaser.nickname  \n"
				+ "        ELSE seller.nickname   \n"
				+ "    END) as nickName ,\n"
				+ "    (CASE \n"
				+ "        WHEN seller.id = :memberId \n"
				+ "        THEN purchaser.image_url  \n"
				+ "        ELSE seller.image_url  \n"
				+ "    END) as imageUrl,\n"
				+ "    (CASE \n"
				+ "        WHEN seller.id = :memberId \n"
				+ "        THEN purchaser.id  \n"
				+ "        ELSE seller.id  \n"
				+ "    END) as memberId, \n"
				+ "    image.image_url as productMainImage, \n"
				+ "    chatroom.id as chatroomid, \n "
				+ "    product.id as productId \n"
				+ "FROM\n"
				+ "    chatroom \n"
				+ "LEFT JOIN\n"
				+ "    product ON chatroom.product_id = product.id \n"
				+ "LEFT JOIN\n"
				+ "    product_image as pi ON chatroom.product_id = pi.product_id AND pi.is_main = 1 \n"
				+ "LEFT JOIN\n"
				+ "\timage ON image.id = pi.image_id\n"
				+ "LEFT JOIN\n"
				+ "    member as seller ON product.member_id = seller.id \n"
				+ "LEFT JOIN\n"
				+ "    member as purchaser ON chatroom.member_id = purchaser.id \n"
				+ "LEFT JOIN\n"
				+ "    location ON product.location_id = location.id \n"
				+ "WHERE\n"
				+ "    product.member_id = :memberId OR purchaser.id = :memberId \n"
				+ "GROUP BY\n"
				+ "    product.member_id,\n"
				+ "    purchaser.nickname,\n"
				+ "    purchaser.image_url,\n"
				+ "    seller.nickname,\n"
				+ "    seller.image_url,\n"
				+ "    location.name,\n"
				+ "    image.image_url,\n"
				+ "    chatroom.id;\n;", nativeQuery = true
	)
	List<ChatroomResponse> findChatRoomsByMemberId(@Param("memberId") Long memberId);

}
