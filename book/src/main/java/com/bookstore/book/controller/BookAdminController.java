package com.bookstore.book.controller;

import com.bookstore.book.BookService;
import com.bookstore.book.dto.BookDto;
import com.bookstore.book.dto.CreateBookRequestDto;
import com.bookstore.book.dto.CreateBookResponseDto;
import com.bookstore.book.dto.UpdateBookRequestDto;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/books")
@RequiredArgsConstructor
public class BookAdminController {

  private final BookService bookService;

  @PostMapping
  public ResponseEntity<CreateBookResponseDto> addBook(
      @Valid @RequestBody CreateBookRequestDto createBookRequestDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bookService.addBook(createBookRequestDto));
  }

  @PatchMapping("/{bookId}")
  public ResponseEntity<BookDto> updateBookById(
      @PathVariable("bookId") UUID bookId, @RequestBody UpdateBookRequestDto updateBookRequestDto) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(bookService.updateBookById(bookId, updateBookRequestDto));
  }

  @DeleteMapping("/{bookId}")
  public ResponseEntity<String> deleteBookById(@PathVariable("bookId") UUID bookId) {
    bookService.deleteBookById(bookId);
    return ResponseEntity.status(HttpStatus.OK).body("Book successfully deleted from the store!");
  }
}
