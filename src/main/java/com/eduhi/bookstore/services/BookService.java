package com.eduhi.bookstore.services;

import com.eduhi.bookstore.dto.BookRecordDto;
import com.eduhi.bookstore.models.BookModel;
import com.eduhi.bookstore.models.ReviewModel;
import com.eduhi.bookstore.repositories.AuthorRepository;
import com.eduhi.bookstore.repositories.BookRepository;
import com.eduhi.bookstore.repositories.PublisherRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public BookModel saveBook(BookRecordDto bookRecordDto) {
        BookModel book = new BookModel();
        book.setTitle(bookRecordDto.title());
        book.setPublisher(publisherRepository
                .findById(bookRecordDto.publisherId())
                .get()
        );
        book.setAuthors(authorRepository
                .findAllById(bookRecordDto.authorIds())
                .stream()
                .collect(Collectors.toSet())
        );

        ReviewModel reviewModel = new ReviewModel();
        reviewModel.setComment(bookRecordDto.reviewComment());
        reviewModel.setBook(book);
        book.setReview(reviewModel);

        return bookRepository.save(book);
    }

    public BookModel getBookById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    public List<BookModel> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional
    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
    }

}
