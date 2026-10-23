package com.eduhi.bookstore.controller;

import com.eduhi.bookstore.dto.AuthorRecordDto;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.AuthorModel;
import com.eduhi.bookstore.services.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookstore/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<AuthorModel> saveAuthor (@RequestBody @Valid AuthorRecordDto authorRecordDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authorService.saveAuthor(authorRecordDto));
    }

    @GetMapping
    public ResponseEntity<List<AuthorModel>> getAllAuthors() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authorService.getAllAuthors());
    }

    @GetMapping("/")
    public ResponseEntity<AuthorModel> getAuthorById(@RequestParam(value = "author-id") UUID id) throws NotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authorService.getAuthorById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Object> updateAuthor(@PathVariable(value = "id") UUID id,
                                               @RequestBody @Valid AuthorRecordDto authorRecordDto) throws NotFoundException {
        AuthorModel authorModel = authorService.getAuthorById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authorService.updateAuthor(
                        authorModel,
                        authorRecordDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable UUID id) {
        authorService.deleteAuthor(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("Author deleted successfully.");
    }
}
