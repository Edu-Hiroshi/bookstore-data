package com.eduhi.bookstore.services;

import com.eduhi.bookstore.dto.BookRecordDto;
import com.eduhi.bookstore.dto.BooksProjection;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.*;
import com.eduhi.bookstore.repositories.AuthorRepository;
import com.eduhi.bookstore.repositories.BookRepository;
import com.eduhi.bookstore.repositories.PublisherRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    public BookService(BookRepository bookRepository,
                       AuthorRepository authorRepository,
                       PublisherRepository publisherRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
    }

    // Transactional allows for a rollback if a problem happens while updating models
    @Transactional
    public BookEntity saveBook(BookRecordDto bookRecordDto) throws NotFoundException {
        PublisherEntity publisher = publisherRepository
                .findById(bookRecordDto.publisherId())
                .orElseThrow(() -> new NotFoundException("Publisher not found."));
        HashSet<AuthorEntity> author = new HashSet<>(authorRepository
                .findAllById(bookRecordDto.authorIds()));

        Instant now = Instant.now();

        BookEntity book = new BookEntity();
        book.setTitle(bookRecordDto.title());
        book.setPublisher(publisher);
        book.setAuthors(author);
        book.setCreated(now);
        book.setUpdated(now);

        ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setComment(bookRecordDto.reviewComment());
        reviewEntity.setBook(book);
        book.setReview(reviewEntity);

        return bookRepository.save(book);
    }

    public List<BookEntity> findBooksByPublisherId(UUID id) {
        return bookRepository.findBooksByPublisherId(id);
    }

    public BookEntity getBookById(UUID id) throws NotFoundException {
        return bookRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found."));
    }

    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<BooksProjection> getAllBooksWithPublisherAndAuthors() {
        return bookRepository.getAllBooksWithPublisherAndAuthors();
    }

    public Page<BooksProjection> getAllBooksWithPublisherAndAuthorsPage(Integer page, Integer size) {
        return bookRepository.getAllBooksWithPublisherAndAuthorsPage(PageRequest.of(page, size));
    }

    // Updates the publisher and author relationships.
    // JPA dirty checking persists changes and removals.
    @Transactional
    public BookEntity updateBook(UUID id, BookRecordDto bookRecordDto) throws NotFoundException {
        BookEntity bookEntity = bookRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found."));

        PublisherEntity publisher = publisherRepository
                .findById(bookRecordDto.publisherId())
                .orElseThrow(() -> new NotFoundException("Publisher not found."));

        HashSet<AuthorEntity> authors = new HashSet<>(authorRepository
                .findAllById(
                    bookRecordDto.authorIds() == null
                        ? Set.of()
                        : bookRecordDto.authorIds()));

        Instant now = Instant.now();

        // removes book from previous publisher and updates current publisher
        PublisherEntity previousPublisher = bookEntity.getPublisher();
        if (previousPublisher != null &&
            !previousPublisher.getId().equals(publisher.getId())) {
            previousPublisher.getBooks().remove(bookEntity);
        }
        publisher.getBooks().add(bookEntity);

        // removes book from previous authors and updates current authors
        for (AuthorEntity previousAuthor : new HashSet<>(bookEntity.getAuthors())) {
            if (!authors.contains(previousAuthor)) {
                previousAuthor.getBooks().remove(bookEntity);
            }
        }
        for (AuthorEntity author : authors) {
            author.getBooks().add(bookEntity);
        }

        bookEntity.setTitle(bookRecordDto.title());
        bookEntity.setPublisher(publisher);
        bookEntity.setAuthors(authors);
        bookEntity.setUpdated(now);

        return bookEntity;
    }

    @Transactional
    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
    }

}
