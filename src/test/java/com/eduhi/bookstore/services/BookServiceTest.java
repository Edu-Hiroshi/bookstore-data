package com.eduhi.bookstore.services;

import com.eduhi.bookstore.dto.BookRecordDto;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.AuthorModel;
import com.eduhi.bookstore.models.BookModel;
import com.eduhi.bookstore.models.PublisherModel;
import com.eduhi.bookstore.repositories.AuthorRepository;
import com.eduhi.bookstore.repositories.BookRepository;
import com.eduhi.bookstore.repositories.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class BookServiceTest {

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private PublisherRepository publisherRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        authorRepository = mock(AuthorRepository.class);
        publisherRepository = mock(PublisherRepository.class);
        bookService = new BookService(bookRepository, authorRepository, publisherRepository);
    }

    @Test
    @DisplayName("Save book with review")
    void saveBookBuildsBookAndReviewAndPersistsIt() throws NotFoundException {
        UUID publisherId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        PublisherModel publisher = new PublisherModel();
        AuthorModel author = new AuthorModel();
        BookRecordDto record = new BookRecordDto(
                "A book",
                publisherId,
                Set.of(authorId),
                "A review");

        when(publisherRepository.findById(publisherId))
                .thenReturn(Optional.of(publisher));
        when(authorRepository.findAllById(record.authorIds()))
                .thenReturn(List.of(author));
        when(bookRepository.save(any(BookModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookModel savedBook = bookService.saveBook(record);

        assertEquals("A book", savedBook.getTitle());
        assertSame(publisher, savedBook.getPublisher());
        assertEquals(Set.of(author), savedBook.getAuthors());
        assertNotNull(savedBook.getCreated());
        assertEquals(savedBook.getCreated(), savedBook.getUpdated());
        assertEquals("A review", savedBook.getReview().getComment());
        assertSame(savedBook, savedBook.getReview().getBook());

        verify(bookRepository).save(savedBook);
        verify(authorRepository).findAllById(record.authorIds());
    }

    @Test
    @DisplayName("[Exception] Save book with non existent publisher")
    void saveBookThrowsWhenPublisherDoesNotExist() {
        UUID publisherId = UUID.randomUUID();
        BookRecordDto record = new BookRecordDto(
                "A book",
                publisherId,
                Set.of(),
                "A review");
        when(publisherRepository.findById(publisherId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookService.saveBook(record));

        assertEquals("Publisher not found.", exception.getMessage());
        verifyNoInteractions(authorRepository, bookRepository);
    }

    @Test
    @DisplayName("Find books by publisher Id")
    void findBooksByPublisherIdReturnsRepositoryResults() {
        UUID publisherId = UUID.randomUUID();
        List<BookModel> books = List.of(new BookModel(), new BookModel());
        when(bookRepository.findBooksByPublisherId(publisherId))
                .thenReturn(books);

        assertSame(books, bookService.findBooksByPublisherId(publisherId));

        verify(bookRepository).findBooksByPublisherId(publisherId);
    }

    @Test
    @DisplayName("Get book by Id")
    void getBookByIdReturnsBookWhenFound() throws NotFoundException {
        UUID bookId = UUID.randomUUID();
        BookModel book = new BookModel();
        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));

        assertSame(book, bookService.getBookById(bookId));

        verify(bookRepository).findById(bookId);
    }

    @Test
    @DisplayName("[Exception] Get book by Id")
    void getBookByIdThrowsWhenBookDoesNotExist() {
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookService.getBookById(bookId));

        assertEquals("Book not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Get all books")
    void getAllBooksReturnsRepositoryResults() {
        List<BookModel> books = List.of(new BookModel(), new BookModel());
        when(bookRepository.findAll())
                .thenReturn(books);

        assertSame(books, bookService.getAllBooks());

        verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("Updates book and associated publisher & author")
    void updateBookUpdatesBookAndAssociations() throws NotFoundException {
        UUID publisherId = UUID.randomUUID();
        PublisherModel publisher = new PublisherModel();
        AuthorModel firstAuthor = new AuthorModel();
        AuthorModel secondAuthor = new AuthorModel();
        BookModel book = new BookModel();

        book.setTitle("Old title");
        Instant previousUpdate = Instant.parse("2020-01-01T00:00:00Z");
        book.setUpdated(previousUpdate);
        BookRecordDto record = new BookRecordDto(
                "New title",
                publisherId,
                Set.of(UUID.randomUUID()),
                null);

        when(publisherRepository.findById(publisherId))
                .thenReturn(Optional.of(publisher));
        when(authorRepository.findAllById(record.authorIds()))
                .thenReturn(List.of(firstAuthor, secondAuthor));
        when(bookRepository.save(any(BookModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookModel updatedBook = bookService.updateBook(book, record);

        assertSame(book, updatedBook);
        assertEquals("New title", book.getTitle());
        assertSame(publisher, book.getPublisher());
        assertEquals(Set.of(firstAuthor, secondAuthor), book.getAuthors());

        assertTrue(book.getUpdated().isAfter(previousUpdate));
        assertTrue(firstAuthor.getBooks().contains(book));
        assertTrue(secondAuthor.getBooks().contains(book));
        assertTrue(publisher.getBooks().contains(book));

        assertNotNull(firstAuthor.getUpdated());
        assertNotNull(secondAuthor.getUpdated());
        assertNotNull(publisher.getUpdated());

        verify(authorRepository).save(firstAuthor);
        verify(authorRepository).save(secondAuthor);
        verify(publisherRepository).save(publisher);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("[Exception] Update book interrupted by error")
    void updateBookThrowsWhenPublisherDoesNotExistWithoutSaving() {
        UUID publisherId = UUID.randomUUID();
        BookModel book = new BookModel();
        BookRecordDto record = new BookRecordDto(
                "New title",
                publisherId,
                Set.of(),
                null);

        when(publisherRepository.findById(publisherId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookService.updateBook(book, record));

        assertEquals("Publisher not found.", exception.getMessage());

        verify(authorRepository, never()).save(any(AuthorModel.class));
        verify(publisherRepository, never()).save(any(PublisherModel.class));
        verify(bookRepository, never()).save(any(BookModel.class));
    }

    @Test
    @DisplayName("Delete book")
    void deleteBookDelegatesToRepository() {
        UUID bookId = UUID.randomUUID();

        bookService.deleteBook(bookId);

        verify(bookRepository).deleteById(bookId);
    }
}