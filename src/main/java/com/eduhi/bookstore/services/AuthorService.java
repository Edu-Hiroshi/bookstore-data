package com.eduhi.bookstore.services;

import com.eduhi.bookstore.dto.AuthorRecordDto;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.AuthorModel;
import com.eduhi.bookstore.models.BookModel;
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

    public AuthorModel getAuthorById(UUID id) throws NotFoundException {
        return authorRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found."));
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

    @Transactional
    public AuthorModel updateAuthor(UUID id, AuthorRecordDto authorRecordDto) throws NotFoundException {
        // get previous IDs of books by the author
        AuthorModel authorModel = authorRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found."));
        Set<BookModel> affectedBooks = new HashSet<>(authorModel.getBooks());

        // get updated IDs of books by the author
        Set<UUID> requestedBookIds = authorRecordDto.bookIds() == null
                ? Set.of()
                : authorRecordDto.bookIds();
        Set<BookModel> requestedBooks = new HashSet<>(bookRepository.findAllById(requestedBookIds));

        affectedBooks.addAll(requestedBooks);

        authorModel.setName(authorRecordDto.name());
        authorModel.setBooks(requestedBooks);
        Instant now = Instant.now();
        authorModel.setUpdated(now);

        // updates author data of individual books to add or remove authors
        for (BookModel book : affectedBooks) {
            boolean changed;
            if (requestedBooks.contains(book)) {
                changed = book.getAuthors().add(authorModel);
            } else {
                changed = book.getAuthors().remove(authorModel);
            }
            if (changed) {
                book.setUpdated(now);
            }
        }
        return authorModel;
    }

    @Transactional
    public void deleteAuthor(UUID id) {
        authorRepository.deleteById(id);
    }
}
