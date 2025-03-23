package com.bookstore.book;

import com.bookstore.book.dto.BookDto;
import com.bookstore.book.dto.CreateBookRequestDto;
import com.bookstore.book.dto.CreateBookResponseDto;
import com.bookstore.book.dto.UpdateBookRequestDto;
import com.bookstore.book.entity.BookEntity;
import com.bookstore.book.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  public List<BookDto> getAllBooks() {
    List<BookEntity> allBooks = bookRepository.findAll();
    return allBooks.stream()
        .map(
            book ->
                BookDto.builder()
                    .bookId(book.getId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .description(book.getDescription())
                    .publicationYear(book.getPublicationYear())
                    .genre(book.getGenre())
                    .price(book.getPrice())
                    .quantityInStock(book.getQuantityInStock())
                    .build())
        .toList();
  }

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
        .publicationYear(book.getPublicationYear())
        .genre(book.getGenre())
        .price(book.getPrice())
        .quantityInStock(book.getQuantityInStock())
        .build();
  }

  public CreateBookResponseDto addBook(CreateBookRequestDto createBookRequestDto) {
    BookEntity book =
        BookEntity.builder()
            .title(createBookRequestDto.getTitle())
            .author(createBookRequestDto.getAuthor())
            .description(createBookRequestDto.getDescription())
            .publicationYear(createBookRequestDto.getPublicationYear())
            .genre(createBookRequestDto.getGenre())
            .price(createBookRequestDto.getPrice())
            .quantityInStock(createBookRequestDto.getQuantity())
            .build();
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

    Optional.ofNullable(updateBookRequestDto.getTitle()).ifPresent(book::setTitle);
    Optional.ofNullable(updateBookRequestDto.getAuthor()).ifPresent(book::setAuthor);
    Optional.ofNullable(updateBookRequestDto.getDescription()).ifPresent(book::setDescription);
    Optional.ofNullable(updateBookRequestDto.getPublicationYear())
        .ifPresent(book::setPublicationYear);
    Optional.ofNullable(updateBookRequestDto.getGenre()).ifPresent(book::setGenre);
    Optional.ofNullable(updateBookRequestDto.getPrice()).ifPresent(book::setPrice);
    Optional.ofNullable(updateBookRequestDto.getQuantity()).ifPresent(book::setQuantityInStock);

    bookRepository.save(book);

    return BookDto.builder()
        .bookId(book.getId())
        .title(book.getTitle())
        .author(book.getAuthor())
        .description(book.getDescription())
        .publicationYear(book.getPublicationYear())
        .genre(book.getGenre())
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
