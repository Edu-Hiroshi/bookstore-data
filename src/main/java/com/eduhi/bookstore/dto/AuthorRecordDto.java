package com.eduhi.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record AuthorRecordDto(
        @NotBlank
        @Size(max = 150)
        String name,

        // TODO: add validation to Set<UUID>
        Set<UUID> bookIds
) {
}
