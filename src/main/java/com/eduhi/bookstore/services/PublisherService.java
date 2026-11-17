package com.eduhi.bookstore.services;

import com.eduhi.bookstore.dto.PublisherRecordDto;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.BookEntity;
import com.eduhi.bookstore.models.PublisherEntity;
import com.eduhi.bookstore.repositories.BookRepository;
import com.eduhi.bookstore.repositories.PublisherRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;

    public PublisherService(PublisherRepository publisherRepository, BookRepository bookRepository) {
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
    }

    public PublisherEntity getPublisherById(UUID id) throws NotFoundException {
        return publisherRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Publisher not found."));
    }

    public List<PublisherEntity> getAllPublishers() {
        return publisherRepository.findAll();
    }

    public PublisherEntity savePublisher(PublisherRecordDto publisherRecordDto) {
        Instant now = Instant.now();

        PublisherEntity publisher = new PublisherEntity();
        publisher.setName(publisherRecordDto.name());
        publisher.setCreated(now);
        publisher.setUpdated(now);

        return publisherRepository.save(publisher);
    }


    @Transactional
    public PublisherEntity updatePublisher(UUID id, PublisherRecordDto publisherRecordDto) throws NotFoundException {
        // get previous IDs of books by the publisher
        PublisherEntity publisherEntity = publisherRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Publisher not found."));
        Set<BookEntity> affectedBooks = new HashSet<>(publisherEntity.getBooks());

        // get updated IDs of books by the publisher
        Set<UUID> requestedBookIds = publisherRecordDto.bookIds() == null
                ? Set.of()
                : publisherRecordDto.bookIds();
        Set<BookEntity> requestedBooks = new HashSet<>(bookRepository.findAllById(requestedBookIds));

        affectedBooks.addAll(requestedBooks);

        publisherEntity.setName(publisherRecordDto.name());
        publisherEntity.setBooks(requestedBooks);
        Instant now = Instant.now();

        // updates publisher data of individual books to add or remove publisher
        for (BookEntity book : affectedBooks) {
            PublisherEntity previousPublisher = book.getPublisher();

            // adds updated publisher to books if previously different or null
            if (requestedBooks.contains(book)) {
                if (previousPublisher == null ||
                    !previousPublisher.getId().equals(publisherEntity.getId())) {

                    if (previousPublisher != null) {
                        previousPublisher.getBooks().remove(book);
                    }

                    book.setPublisher(publisherEntity);
                    book.setUpdated(now);
                }

            // removes outdated publisher from book
            } else if (previousPublisher != null &&
                       previousPublisher.getId().equals(publisherEntity.getId())) {

                book.setPublisher(null);
                book.setUpdated(now);
            }
        }

        return publisherEntity;
    }

    @Transactional
    public void deletePublisher(UUID id) {
        publisherRepository.deleteById(id);
    }
}
