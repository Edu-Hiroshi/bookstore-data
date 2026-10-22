package com.eduhi.bookstore.exception;

public record ErrorResponseDto(
        String message,
        Integer Status) {
}
