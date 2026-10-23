package com.eduhi.bookstore.controller;

import com.eduhi.bookstore.dto.PublisherRecordDto;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.PublisherModel;
import com.eduhi.bookstore.services.PublisherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookstore/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping
    public ResponseEntity<PublisherModel> savePublisher (@RequestBody @Valid PublisherRecordDto publisherRecordDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(publisherService.savePublisher(publisherRecordDto));
    }

    @GetMapping
    public ResponseEntity<List<PublisherModel>> getAllPublishers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(publisherService.getAllPublishers());
    }

    @GetMapping("/")
    public ResponseEntity<PublisherModel> getPublisherById(@RequestParam(value = "publisher-id") UUID id) throws NotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(publisherService.getPublisherById(id));
    }
}
