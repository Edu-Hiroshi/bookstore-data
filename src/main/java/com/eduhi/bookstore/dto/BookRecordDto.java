package com.eduhi.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record BookRecordDto(

        @NotBlank
        @Size(max = 150)
        String title,

        @NotBlank
        @Size(max = 100)
        UUID publisherId,

        @NotBlank
        @Size(max = 100)
        Set<UUID> authorIds,

        @Size(max = 2000)
        String reviewComment) {
}
