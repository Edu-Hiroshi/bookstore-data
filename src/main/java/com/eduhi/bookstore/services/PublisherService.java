package com.eduhi.bookstore.services;

import com.eduhi.bookstore.dto.PublisherRecordDto;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.BookModel;
import com.eduhi.bookstore.models.PublisherModel;
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

    public PublisherModel getPublisherById(UUID id) throws NotFoundException {
        return publisherRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Publisher not found."));
    }

    public List<PublisherModel> getAllPublishers() {
        return publisherRepository.findAll();
    }

    public PublisherModel savePublisher(PublisherRecordDto publisherRecordDto) {
        Instant now = Instant.now();

        PublisherModel publisher = new PublisherModel();
        publisher.setName(publisherRecordDto.name());
        publisher.setCreated(now);
        publisher.setUpdated(now);

        return publisherRepository.save(publisher);
    }


    public PublisherModel updatePublisher(PublisherModel publisherModel, PublisherRecordDto publisherRecordDto) {
        List<BookModel> bookList = bookRepository.findAllById(publisherRecordDto.bookIds());
        Set<BookModel> books = new HashSet<BookModel>(bookList);

        publisherModel.setName(publisherRecordDto.name());
        publisherModel.setBooks(books);
        publisherModel.setUpdated(Instant.now());

        //TODO: add logic to remove publishing

        for (BookModel book : bookList) {
            PublisherModel publisher = book.getPublisher();
            book.setPublisher(publisher);
            book.setUpdated(Instant.now());
            bookRepository.save(book);
        }

        return publisherRepository.save(publisherModel);
    }

    @Transactional
    public void deletePublisher(UUID id) {
        publisherRepository.deleteById(id);
    }
}
