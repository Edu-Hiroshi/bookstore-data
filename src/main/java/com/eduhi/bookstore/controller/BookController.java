package com.eduhi.bookstore.controller;

import com.eduhi.bookstore.dto.BookRecordDto;
import com.eduhi.bookstore.models.BookModel;
import com.eduhi.bookstore.services.BookService;
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
    public ResponseEntity<BookModel> saveBook (@RequestBody BookRecordDto bookRecordDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookService.saveBook(bookRecordDto));
    }

    @GetMapping("/book_id/{id}")
    public ResponseEntity<BookModel> getBookById(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookService.getBookById(id));
    }

    @GetMapping("/publisher_id/{id}")
    public ResponseEntity<List<BookModel>> findBooksByPublisherId(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookService.getAllBooks());
    }

    @GetMapping
    public ResponseEntity<List<BookModel>> getAllBooks() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookService.getAllBooks());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Book deleted successfully.");
    }
}
