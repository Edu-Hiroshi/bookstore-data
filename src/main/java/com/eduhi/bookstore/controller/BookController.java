package com.eduhi.bookstore.controller;

import com.eduhi.bookstore.dto.BookRecordDto;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.BookModel;
import com.eduhi.bookstore.services.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookstore/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookModel> saveBook (@RequestBody @Valid BookRecordDto bookRecordDto) throws NotFoundException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookService.saveBook(bookRecordDto));
    }

    @GetMapping("/")
    public ResponseEntity<BookModel> getBookById(@RequestParam(value = "book-id") UUID id) throws NotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookService.getBookById(id));
    }

    @GetMapping("/publisher_id/{id}")
    public ResponseEntity<List<BookModel>> findBooksByPublisherId(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookService.findBooksByPublisherId(id));
    }

    @GetMapping
    public ResponseEntity<List<BookModel>> getAllBooks() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookService.getAllBooks());
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Object> updateBook(@PathVariable(value = "id") UUID id,
                                             @RequestBody @Valid BookRecordDto bookRecordDto) throws NotFoundException {
        BookModel bookModel = bookService.getBookById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookService.updateBook(
                        bookModel,
                        bookRecordDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("Book deleted successfully.");
    }
}
