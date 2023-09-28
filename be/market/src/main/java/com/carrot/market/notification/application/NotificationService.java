package com.carrot.market.notification.application;

import org.springframework.stereotype.Service;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.MemberException;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.notification.domain.NotificationEmitters;

@Service
public class NotificationService {

	private final NotificationEmitters notificationEmitters;
	private final MemberRepository memberRepository;

	public NotificationService(MemberRepository memberRepository) {
		this.notificationEmitters = new NotificationEmitters();
		this.memberRepository = memberRepository;
	}

	public void subscribe(Long memberId) {
		memberRepository.findById(memberId)
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
		notificationEmitters.add(memberId);
	}
}
