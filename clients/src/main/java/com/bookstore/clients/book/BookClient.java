package com.bookstore.clients.book;

import com.bookstore.clients.book.dto.BookDto;
import com.bookstore.clients.book.dto.UpdateBookRequestDto;
import com.bookstore.clients.config.FeignConfig;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "book", url = "${clients.book.url}", configuration = FeignConfig.class)
public interface BookClient {

  @GetMapping("/api/v1/books/{bookId}")
  BookDto getBookById(@PathVariable("bookId") UUID bookId);

  @PatchMapping("/api/v1/admin/books/{bookId}")
  BookDto updateBookById(
      @PathVariable("bookId") UUID bookId,
      @Valid @RequestBody UpdateBookRequestDto updateBookRequestDto);
}
