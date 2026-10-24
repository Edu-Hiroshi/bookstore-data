package com.eduhi.bookstore.services;

import com.eduhi.bookstore.dto.BookRecordDto;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.AuthorModel;
import com.eduhi.bookstore.models.BookModel;
import com.eduhi.bookstore.models.PublisherModel;
import com.eduhi.bookstore.models.ReviewModel;
import com.eduhi.bookstore.repositories.AuthorRepository;
import com.eduhi.bookstore.repositories.BookRepository;
import com.eduhi.bookstore.repositories.PublisherRepository;
import jakarta.transaction.Transactional;
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

    // - set the information needed to create a book in the database (publisher, author, etc.)
    // - transactional allows for a rollback if a problem happens
    @Transactional
    public BookModel saveBook(BookRecordDto bookRecordDto) throws NotFoundException {
        PublisherModel publisher = publisherRepository
                .findById(bookRecordDto.publisherId())
                .orElseThrow(() -> new NotFoundException("Publisher not found."));
        HashSet<AuthorModel> author = new HashSet<>(authorRepository
                .findAllById(bookRecordDto.authorIds()));

        Instant now = Instant.now();

        BookModel book = new BookModel();
        book.setTitle(bookRecordDto.title());
        book.setPublisher(publisher);
        book.setAuthors(author);
        book.setCreated(now);
        book.setUpdated(now);

        ReviewModel reviewModel = new ReviewModel();
        reviewModel.setComment(bookRecordDto.reviewComment());
        reviewModel.setBook(book);
        book.setReview(reviewModel);

        return bookRepository.save(book);
    }

    public List<BookModel> findBooksByPublisherId(UUID id) {
        return bookRepository.findBooksByPublisherId(id);
    }

    public BookModel getBookById(UUID id) throws NotFoundException {
        return bookRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found."));
    }

    public List<BookModel> getAllBooks() {
        return bookRepository.findAll();
    }

    public BookModel updateBook(BookModel bookModel, BookRecordDto bookRecordDto) throws NotFoundException {
        PublisherModel publisher = publisherRepository
                .findById(bookRecordDto.publisherId())
                .orElseThrow(() -> new NotFoundException("Publisher not found."));
        HashSet<AuthorModel> authors = new HashSet<>(authorRepository
                .findAllById(bookRecordDto.authorIds()));

        bookModel.setTitle(bookRecordDto.title());
        bookModel.setPublisher(publisher);
        bookModel.setAuthors(authors);
        bookModel.setUpdated(Instant.now());

        //TODO: add logic to remove books from authors and publishers

        for (AuthorModel author : authors){
            Set<BookModel> authorBooks = author.getBooks();
            authorBooks.add(bookModel);
            author.setBooks(authorBooks);
            author.setUpdated(Instant.now());
            authorRepository.save(author);
        }

        Set<BookModel> publisherBooks = publisher.getBooks();
        publisherBooks.add(bookModel);
        publisher.setBooks(publisherBooks);
        publisher.setUpdated(Instant.now());
        publisherRepository.save(publisher);

        return bookRepository.save(bookModel);
    }

    @Transactional
    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
    }

}
