package com.carrot.market.member.application;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.MemberException;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.support.IntegrationTestSupport;

class MemberServiceTest extends IntegrationTestSupport {
	@Autowired
	MemberService memberService;
	@Autowired
	MemberRepository memberRepository;

	@Test
	void checkDuplicateNickname() {
		// given
		Member june = Member.builder().nickname("June").build();
		memberRepository.save(june);

		// when & then
		memberService.checkDuplicateNickname("Nickname");
	}

	@Test
	void checkExistNicknameInvokeException() {
		// given
		Member june = Member.builder().nickname("June12").build();
		memberRepository.save(june);

		// when
		assertThatThrownBy(
			() -> memberService.checkDuplicateNickname(june.getNickname())).isInstanceOf(
			ApiException.class).hasMessage(MemberException.EXIST_MEMBER.getMessage());
	}
}