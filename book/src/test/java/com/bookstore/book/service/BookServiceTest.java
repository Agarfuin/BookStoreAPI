package com.bookstore.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bookstore.book.BookService;
import com.bookstore.book.dto.BookDto;
import com.bookstore.book.dto.CreateBookRequestDto;
import com.bookstore.book.dto.CreateBookResponseDto;
import com.bookstore.book.dto.UpdateBookRequestDto;
import com.bookstore.book.entity.BookEntity;
import com.bookstore.book.enums.Genre;
import com.bookstore.book.repository.BookRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock private BookRepository bookRepository;

  @InjectMocks private BookService bookService;

  private BookEntity bookEntity;
  private static UUID bookId;

  @BeforeEach
  void setUp() {
    bookId = UUID.fromString("2c6c6f07-b17b-4e52-86ab-b356c515a556"); // Test Book ID
    bookEntity = BookEntity.builder().id(bookId).title("Test Book").author("Test Author").build();
  }

  @Test
  void getAllBooks_ShouldReturnListOfBookDtos() {
    when(bookRepository.findAll()).thenReturn(List.of(bookEntity));

    List<BookDto> result = bookService.getAllBooks();

    assertFalse(result.isEmpty());
    assertEquals(bookEntity.getTitle(), result.get(0).getTitle());
  }

  @Test
  void getBookById_ShouldReturnBookDto() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));

    BookDto result = bookService.getBookById(bookId);

    assertNotNull(result);
    assertEquals(bookEntity.getTitle(), result.getTitle());
  }

  @Test
  void getBookById_WhenBookNotFound_ShouldThrowResponseStatusException() {
    when(bookRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(
        ResponseStatusException.class,
        () ->
            bookService.getBookById(
                UUID.fromString("8a085df8-ba16-44cf-aa1f-8abad8549c95"))); // Test Wrong Book ID
  }

  @Test
  void addBook_ShouldReturnCreateBookResponseDto() {
    CreateBookRequestDto createBookRequestDto =
        CreateBookRequestDto.builder()
            .title("New Test Book")
            .author("New Test Author")
            .description("New Test Description")
            .publicationYear(2025)
            .genre(Set.of(Genre.CLASSIC))
            .price(BigDecimal.valueOf(29.99))
            .quantity(50)
            .build();

    when(bookRepository.saveAndFlush(any(BookEntity.class))).thenReturn(bookEntity);

    CreateBookResponseDto result = bookService.addBook(createBookRequestDto);

    ArgumentCaptor<BookEntity> bookEntityCaptor = ArgumentCaptor.forClass(BookEntity.class);
    verify(bookRepository, times(1)).saveAndFlush(bookEntityCaptor.capture());

    BookEntity capturedBookEntity = bookEntityCaptor.getValue();
    assertEquals(createBookRequestDto.getTitle(), capturedBookEntity.getTitle());
    assertEquals(createBookRequestDto.getAuthor(), capturedBookEntity.getAuthor());
    assertEquals(createBookRequestDto.getDescription(), capturedBookEntity.getDescription());
    assertEquals(
        createBookRequestDto.getPublicationYear(), capturedBookEntity.getPublicationYear());
    assertEquals(createBookRequestDto.getGenre(), capturedBookEntity.getGenre());
    assertEquals(createBookRequestDto.getPrice(), capturedBookEntity.getPrice());
    assertEquals(createBookRequestDto.getQuantity(), capturedBookEntity.getQuantityInStock());

    assertNotNull(result);
  }

  @Test
  void updateBookById_ShouldUpdateBookAndReturnUpdatedBookDto() {
    UpdateBookRequestDto updateBookRequestDto =
        UpdateBookRequestDto.builder()
            .title("Updated Test Title")
            .author("Updated Test Author")
            .description("Updated Test Description")
            .publicationYear(2024)
            .genre(Set.of(Genre.ADVENTURE))
            .price(BigDecimal.valueOf(39.99))
            .quantity(120)
            .build();

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
    when(bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);

    BookDto result = bookService.updateBookById(bookId, updateBookRequestDto);

    ArgumentCaptor<BookEntity> bookEntityCaptor = ArgumentCaptor.forClass(BookEntity.class);
    verify(bookRepository, times(1)).save(bookEntityCaptor.capture());

    BookEntity capturedBookEntity = bookEntityCaptor.getValue();
    assertEquals(updateBookRequestDto.getTitle(), capturedBookEntity.getTitle());
    assertEquals(updateBookRequestDto.getAuthor(), capturedBookEntity.getAuthor());
    assertEquals(updateBookRequestDto.getDescription(), capturedBookEntity.getDescription());
    assertEquals(
        updateBookRequestDto.getPublicationYear(), capturedBookEntity.getPublicationYear());
    assertEquals(updateBookRequestDto.getGenre(), capturedBookEntity.getGenre());
    assertEquals(updateBookRequestDto.getPrice(), capturedBookEntity.getPrice());
    assertEquals(updateBookRequestDto.getQuantity(), capturedBookEntity.getQuantityInStock());

    assertNotNull(result);
    assertEquals(bookEntity.getId(), result.getBookId());
  }

  @Test
  void deleteBookById_ShouldDeleteBook() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));

    bookService.deleteBookById(bookId);

    verify(bookRepository, times(1)).delete(bookEntity);
  }

  @Test
  void deleteBookById_WhenBookNotFound_ShouldThrowResponseStatusException() {
    when(bookRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(
        ResponseStatusException.class,
        () ->
            bookService.deleteBookById(
                UUID.fromString("8a085df8-ba16-44cf-aa1f-8abad8549c95"))); // Test Wrong Book ID
  }
}
