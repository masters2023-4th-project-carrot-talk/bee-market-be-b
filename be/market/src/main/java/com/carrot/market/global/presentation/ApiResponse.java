package com.carrot.market.global.presentation;

public record ApiResponse<T>(
	boolean success,
	T data
) {

	public static <T> ApiResponse<T> successNoBody() {
		return new ApiResponse<>(true, null);
	}

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, data);
	}
}
