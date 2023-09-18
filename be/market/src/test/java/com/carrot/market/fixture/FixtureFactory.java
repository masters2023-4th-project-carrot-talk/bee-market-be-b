package com.carrot.market.fixture;

import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.image.domain.Image;
import com.carrot.market.location.domain.Location;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.domain.WishList;
import com.carrot.market.product.domain.Category;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.domain.ProductDetails;
import com.carrot.market.product.domain.ProductImage;
import com.carrot.market.product.domain.SellingStatus;

public class FixtureFactory {
	public static Product makeProduct(Member member, Location location, Category category,
		SellingStatus status,
		ProductDetails productDetails) {
		return Product.builder()
			.category(category)
			.seller(member)
			.location(location)
			.status(status)
			.productDetails(productDetails)
			.build();
	}

	public static ProductImage makeProductImage(Product product, Image image, boolean isMain) {
		return ProductImage.builder().product(product).image(image).isMain(isMain).build();
	}

	public static WishList makeWishList(Product product, Member member) {
		return WishList.builder().product(product).member(member).build();
	}

	public static Location makeLocation(String name) {
		return new Location(name);
	}

	public static Category makeCategory(String name, String imageUrl) {
		return Category.builder().name(name).imageUrl(imageUrl).build();
	}

	public static Chatroom makeChatRoom(Product product, Member purchaser) {
		return Chatroom.builder().product(product).purchaser(purchaser).build();
	}

	public static Image makeImage(String imageUrl) {
		return new Image(imageUrl);
	}

	public static Member makeMember(String nickname, String imageUrl) {
		return Member.builder().imageUrl(imageUrl).nickname(nickname).build();
	}

}

