package com.carrot.market.notification.domain;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Notification implements Serializable {
	private static final String NOTIFICATION_CONTENT_SEPARATOR = ": ";

	private String title;
	private String content;

	public static Notification create(String title, String sender, String content) {
		return new Notification(title,
			sender + NOTIFICATION_CONTENT_SEPARATOR + content);
	}
}
