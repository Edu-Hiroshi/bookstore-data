package com.eduhi.bookstore.services;

import com.eduhi.bookstore.dto.AuthorRecordDto;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.AuthorEntity;
import com.eduhi.bookstore.models.BookEntity;
import com.eduhi.bookstore.repositories.AuthorRepository;
import com.eduhi.bookstore.repositories.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public AuthorEntity getAuthorById(UUID id) throws NotFoundException {
        return authorRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found."));
    }

    public List<AuthorEntity> getAllAuthors() {
        return authorRepository.findAll();
    }

    public AuthorEntity saveAuthor(AuthorRecordDto authorRecordDto) {
        Instant now = Instant.now();

        AuthorEntity author = new AuthorEntity();
        author.setName(authorRecordDto.name());
        author.setCreated(now);
        author.setUpdated(now);

        return authorRepository.save(author);
    }

    @Transactional
    public AuthorEntity updateAuthor(UUID id, AuthorRecordDto authorRecordDto) throws NotFoundException {
        // get previous IDs of books by the author
        AuthorEntity authorEntity = authorRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found."));
        Set<BookEntity> affectedBooks = new HashSet<>(authorEntity.getBooks());

        // get updated IDs of books by the author
        Set<UUID> requestedBookIds = authorRecordDto.bookIds() == null
                ? Set.of()
                : authorRecordDto.bookIds();
        Set<BookEntity> requestedBooks = new HashSet<>(bookRepository.findAllById(requestedBookIds));

        affectedBooks.addAll(requestedBooks);

        authorEntity.setName(authorRecordDto.name());
        authorEntity.setBooks(requestedBooks);
        Instant now = Instant.now();
        authorEntity.setUpdated(now);

        // updates author data of individual books to add or remove authors
        for (BookEntity book : affectedBooks) {
            boolean changed;
            if (requestedBooks.contains(book)) {
                changed = book.getAuthors().add(authorEntity);
            } else {
                changed = book.getAuthors().remove(authorEntity);
            }
            if (changed) {
                book.setUpdated(now);
            }
        }
        return authorEntity;
    }

    @Transactional
    public void deleteAuthor(UUID id) {
        authorRepository.deleteById(id);
    }
}
