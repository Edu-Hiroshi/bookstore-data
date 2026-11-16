package com.eduhi.bookstore.dto;

import com.eduhi.bookstore.models.AuthorModel;
import com.eduhi.bookstore.models.BookModel;

import java.util.Set;
import java.util.UUID;

public interface BooksProjection {
    UUID getId();
    String getTitle();
    String getPublisher();
    Set<String> getAuthors();
}
