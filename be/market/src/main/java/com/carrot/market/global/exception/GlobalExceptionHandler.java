package com.carrot.market.global.exception;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.carrot.market.global.exception.domain.ImageException;
import com.carrot.market.global.exception.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ErrorResponse> apiExceptionHandler(ApiException ex) {
		return ResponseEntity.status(ex.getStatus())
			.body(ErrorResponse.fail(ex.getStatus(), ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponse validationExceptionHandler(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("유효하지 않은 값입니다."))
			.findAny()
			.orElseThrow();

		return ErrorResponse.fail(HttpStatus.BAD_REQUEST.value(), message);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> exceededUploadSizeExceptionHandler() {
		ImageException imageException = ImageException.MAX_UPLOAD_SIZE_EXCEEDED;

		return ResponseEntity.status(imageException.getHttpStatus()).body(ErrorResponse.fail(
			imageException.getHttpStatus().value(),
			imageException.getMessage()));
	}
}
