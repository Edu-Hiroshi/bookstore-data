package com.eduhi.bookstore;

import com.eduhi.bookstore.dto.AuthorRecordDto;
import com.eduhi.bookstore.dto.BookRecordDto;
import com.eduhi.bookstore.dto.PublisherRecordDto;
import com.eduhi.bookstore.models.*;
import com.eduhi.bookstore.repositories.AuthorRepository;
import com.eduhi.bookstore.repositories.BookRepository;
import com.eduhi.bookstore.repositories.PublisherRepository;
import com.eduhi.bookstore.repositories.ReviewRepository;
import com.eduhi.bookstore.services.AuthorService;
import com.eduhi.bookstore.services.BookService;
import com.eduhi.bookstore.services.PublisherService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BookstoreApplicationTests {

	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private AuthorRepository authorRepository;
	@Autowired
	private PublisherRepository publisherRepository;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private BookService bookService;
	@Autowired
	private AuthorService authorService;
	@Autowired
	private PublisherService publisherService;
	@Autowired
	private EntityManager entityManager;

    // Integration coverage tests (non-mock).

    // Starts the Spring application context, checks that the
    // application’s components are wired together.
	@Test
	void contextLoads() {
	}

    // Association test: persists book–publisher and book–author changes
    // through JPA repositories and the EntityManager.
    // Flushes and clears the persistence context, then reloads the book and checks
    // the database state, deletion behavior for reviews, authors, and publishers.
	@Test
	@Transactional
	void associationUpdatesPersistAddedAndRemovedRelationships() throws Exception {
		Instant now = Instant.now();
		PublisherEntity oldPublisher = savePublisher("Old publisher", now);
		PublisherEntity newPublisher = savePublisher("New publisher", now);
		AuthorEntity oldAuthor = saveAuthor("Old author", now);
		AuthorEntity newAuthor = saveAuthor("New author", now);

		BookEntity book = new BookEntity();
		book.setTitle("Association test book");
		book.setPublisher(oldPublisher);
		book.setAuthors(Set.of(oldAuthor));
		book.setCreated(now);
		book.setUpdated(now);
		ReviewEntity review = new ReviewEntity();
		review.setComment("Review");
		review.setBook(book);
		book.setReview(review);
		book = bookRepository.save(book);
		var reviewId = review.getId();

		bookService.updateBook(book.getId(), new BookRecordDto(
				"Association test book",
				newPublisher.getId(),
				Set.of(newAuthor.getId()),
				"Review"));

		entityManager.flush();
		entityManager.clear();

		BookEntity updatedBook = bookRepository.findById(book.getId()).orElseThrow();

		assertEquals(newPublisher.getId(),
                     updatedBook.getPublisher().getId());
		assertEquals(Set.of(newAuthor.getId()),
				     updatedBook.getAuthors()
                             .stream().map(AuthorEntity::getId).collect(java.util.stream.Collectors.toSet()));

		authorService.updateAuthor(
                newAuthor.getId(),
                new AuthorRecordDto("New author", Set.of()));

        publisherService.updatePublisher(
                newPublisher.getId(),
                new PublisherRecordDto("New publisher", Set.of()));

		entityManager.flush();
		entityManager.clear();

        BookEntity detachedBook = bookRepository.findById(book.getId()).orElseThrow();

		assertNull(detachedBook.getPublisher());
		assertEquals(Set.of(),
					 detachedBook.getAuthors());
		assertEquals(oldAuthor.getId(),
					 authorRepository.findById(oldAuthor.getId()).orElseThrow().getId());
		assertEquals(oldPublisher.getId(),
					 publisherRepository.findById(oldPublisher.getId()).orElseThrow().getId());

		bookService.deleteBook(book.getId());

		entityManager.flush();
		entityManager.clear();

		assertFalse(bookRepository.existsById(book.getId()));
		assertFalse(reviewRepository.existsById(reviewId));
		assertTrue(authorRepository.existsById(newAuthor.getId()));
		assertTrue(publisherRepository.existsById(newPublisher.getId()));
	}

	private PublisherEntity savePublisher(String name, Instant now) {
		PublisherEntity publisher = new PublisherEntity();
		publisher.setName(name);
		publisher.setCreated(now);
		publisher.setUpdated(now);
		return publisherRepository.save(publisher);
	}

	private AuthorEntity saveAuthor(String name, Instant now) {
		AuthorEntity author = new AuthorEntity();
		author.setName(name);
		author.setCreated(now);
		author.setUpdated(now);
		return authorRepository.save(author);
	}
}
