package com.bookstore.book;

import com.bookstore.book.dto.BookDto;
import com.bookstore.book.dto.CreateBookRequestDto;
import com.bookstore.book.dto.CreateBookResponseDto;
import com.bookstore.book.dto.UpdateBookRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping("/hello-world")
  @ResponseStatus(HttpStatus.OK)
  public String helloWorld() {
    return "Hello World";
  }

  @GetMapping("/{bookId}")
  public ResponseEntity<BookDto> getBookById(@PathVariable("bookId") UUID bookId) {
    return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookById(bookId));
  }

  @PostMapping("/add")
  public ResponseEntity<CreateBookResponseDto> addBook(
      @RequestBody CreateBookRequestDto createBookRequestDto) {
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
