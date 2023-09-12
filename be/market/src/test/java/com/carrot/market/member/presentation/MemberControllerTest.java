package com.carrot.market.member.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.MemberException;
import com.carrot.market.support.ControllerTestSupport;

class MemberControllerTest extends ControllerTestSupport {

	@Test
	void checkDuplicateNickname() throws Exception {
		// given
		when(authService.checkDuplicateNickname(any())).thenReturn(true);

		// when & then
		mockMvc.perform(
				get("/api/users/nickname").param("nickname", "jun")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value("true"));
	}

	@Test
	void checkDuplicateNicknameInvokeExistMemberException() throws Exception {
		// given
		when(authService.checkDuplicateNickname(any())).thenThrow(
			new ApiException(MemberException.EXIST_MEMBER));

		// when & then
		mockMvc.perform(
				get("/api/users/nickname").param("nickname", "jun")
			)
			.andDo(print())
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.success").value("false"))
			.andExpect(jsonPath("$.errorCode.status").value(MemberException.EXIST_MEMBER.getHttpStatus().value()))
			.andExpect(jsonPath("$.errorCode.message").value(MemberException.EXIST_MEMBER.getMessage()));
	}
}