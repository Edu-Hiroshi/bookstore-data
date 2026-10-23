package com.eduhi.bookstore.services;

import com.eduhi.bookstore.dto.PublisherRecordDto;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.PublisherModel;
import com.eduhi.bookstore.repositories.PublisherRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
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
}
