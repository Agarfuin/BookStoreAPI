package com.bookstore.book;

import com.bookstore.book.dto.BookDto;
import com.bookstore.book.dto.CreateBookRequestDto;
import com.bookstore.book.dto.CreateBookResponseDto;
import com.bookstore.book.dto.UpdateBookRequestDto;
import com.bookstore.book.entity.BookEntity;
import com.bookstore.book.repository.BookRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  public BookDto getBookById(UUID bookId) {
    BookEntity book =
        bookRepository
            .findById(bookId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("No book with id %s found", bookId)));

    return BookDto.builder()
        .bookId(book.getId())
        .title(book.getTitle())
        .author(book.getAuthor())
        .description(book.getDescription())
        .price(book.getPrice())
        .quantityInStock(book.getQuantityInStock())
        .build();
  }

  public CreateBookResponseDto addBook(CreateBookRequestDto createBookRequestDto) {
    BookEntity book =
        bookRepository
            .findById(createBookRequestDto.getBookId())
            .orElseGet(
                () -> {
                  BookEntity newBook =
                      BookEntity.builder()
                          .title(createBookRequestDto.getTitle())
                          .author(createBookRequestDto.getAuthor())
                          .description(createBookRequestDto.getDescription())
                          .price(createBookRequestDto.getPrice())
                          .quantityInStock(0)
                          .build();
                  return bookRepository.save(newBook);
                });
    book.setQuantityInStock(book.getQuantityInStock() + createBookRequestDto.getQuantity());

    bookRepository.saveAndFlush(book);
    return CreateBookResponseDto.builder().bookId(book.getId()).build();
  }

  public BookDto updateBookById(UUID bookId, UpdateBookRequestDto updateBookRequestDto) {
    BookEntity book =
        bookRepository
            .findById(bookId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("No book with id %s found", bookId)));

    book.setTitle(
        updateBookRequestDto.getTitle() == null
            ? book.getTitle()
            : updateBookRequestDto.getTitle());
    book.setAuthor(
        updateBookRequestDto.getAuthor() == null
            ? book.getAuthor()
            : updateBookRequestDto.getAuthor());
    book.setDescription(
        updateBookRequestDto.getDescription() == null
            ? book.getDescription()
            : updateBookRequestDto.getDescription());
    book.setPrice(
        updateBookRequestDto.getPrice() == null
            ? book.getPrice()
            : updateBookRequestDto.getPrice());
    book.setQuantityInStock(
        updateBookRequestDto.getQuantity() == null || updateBookRequestDto.getQuantity() < 0
            ? book.getQuantityInStock()
            : updateBookRequestDto.getQuantity());

    bookRepository.save(book);

    return BookDto.builder()
        .bookId(book.getId())
        .title(book.getTitle())
        .author(book.getAuthor())
        .description(book.getDescription())
        .price(book.getPrice())
        .quantityInStock(book.getQuantityInStock())
        .build();
  }

  public void deleteBookById(UUID bookId) {
    BookEntity book =
        bookRepository
            .findById(bookId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("No book with id %s found", bookId)));

    bookRepository.delete(book);
  }
}
