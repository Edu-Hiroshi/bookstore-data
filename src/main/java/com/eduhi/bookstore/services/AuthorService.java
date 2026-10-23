package com.eduhi.bookstore.services;


import com.eduhi.bookstore.dto.AuthorRecordDto;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.AuthorModel;
import com.eduhi.bookstore.models.BookModel;
import com.eduhi.bookstore.repositories.AuthorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public AuthorModel getAuthorById(UUID id) throws NotFoundException {
        return authorRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found."));
    }

    public List<AuthorModel> getAllAuthors() {
        return authorRepository.findAll();
    }

    public AuthorModel saveAuthor(AuthorRecordDto authorRecordDto) {
        Instant now = Instant.now();

        AuthorModel author = new AuthorModel();
        author.setName(authorRecordDto.name());
        author.setCreated(now);
        author.setUpdated(now);

        return authorRepository.save(author);
    }
}
