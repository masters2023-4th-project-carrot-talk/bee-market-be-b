package com.carrot.market.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
	@NotBlank
	String code
) {
}
