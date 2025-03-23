package com.bookstore.book.controller;

import com.bookstore.book.BookService;
import com.bookstore.book.dto.BookDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping("/hello-world")
  @ResponseStatus(HttpStatus.OK)
  public String helloWorld() {
    return "Hello World";
  }

  @GetMapping
  public ResponseEntity<List<BookDto>> getAllBooks() {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks());
  }

  @GetMapping("/{bookId}")
  public ResponseEntity<BookDto> getBookById(@PathVariable("bookId") UUID bookId) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookById(bookId));
  }
}
