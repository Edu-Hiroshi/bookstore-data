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

    public AuthorModel updateAuthor(AuthorModel authorModel, AuthorRecordDto authorRecordDto) {
        List<BookModel> bookList = bookRepository.findAllById(authorRecordDto.bookIds());
        Set<BookModel> books = new HashSet<BookModel>(bookList);

        authorModel.setName(authorRecordDto.name());
        authorModel.setBooks(books);
        authorModel.setUpdated(Instant.now());

        //TODO: add logic to remove authorship

        for (BookModel book : bookList) {
            Set<AuthorModel> authors = book.getAuthors();
            authors.add(authorModel);
            book.setAuthors(authors);
            book.setUpdated(Instant.now());
            bookRepository.save(book);
        }
        return authorRepository.save(authorModel);
    }

    @Transactional
    public void deleteAuthor(UUID id) {
        authorRepository.deleteById(id);
    }
}
