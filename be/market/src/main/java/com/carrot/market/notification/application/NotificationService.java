package com.carrot.market.notification.application;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.MemberException;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.notification.domain.Notification;
import com.carrot.market.notification.domain.NotificationEmitters;

@Service
public class NotificationService {

	private final MemberRepository memberRepository;
	private final NotificationEmitters notificationEmitters;

	public NotificationService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
		this.notificationEmitters = new NotificationEmitters();
	}

	public SseEmitter subscribe(Long memberId) {
		memberRepository.findById(memberId)
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
		return notificationEmitters.add(memberId);
	}

	public void send(Member receiver, Notification notification) {
		notificationEmitters.send(receiver.getId(), notification);
	}
}
