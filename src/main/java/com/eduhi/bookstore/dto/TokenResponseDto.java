package com.eduhi.bookstore.dto;

public record TokenResponseDto(
        String token,
        long expiresIn
) {
}
