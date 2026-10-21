package com.eduhi.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record BookRecordDto(

        @NotBlank
        @Size(max = 150)
        String title,
        
        UUID publisherId,

        Set<UUID> authorIds,

        @Size(max = 2000)
        String reviewComment) {
}
