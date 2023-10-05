package com.carrot.market.notification.presentation;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.carrot.market.member.presentation.annotation.Login;
import com.carrot.market.member.resolver.MemberId;
import com.carrot.market.notification.application.NotificationService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/notification")
@RequiredArgsConstructor
@RestController
public class NotificationController {
	private static final String NGINX_BUFFERING_OPTION_HEADER = "X-Accel-Buffering";
	private static final String DISABLED = "no";

	private final NotificationService notificationService;

	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<SseEmitter> subscribe(
		@Login MemberId memberId
	) {
		final SseEmitter sseEmitter = notificationService.subscribe(memberId.getMemberID());
		
		return ResponseEntity.ok()
			.header(NGINX_BUFFERING_OPTION_HEADER, DISABLED)
			.body(sseEmitter);
	}
}
