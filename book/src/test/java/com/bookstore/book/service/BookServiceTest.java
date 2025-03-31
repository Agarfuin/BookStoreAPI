package com.bookstore.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bookstore.book.BookService;
import com.bookstore.book.dto.BookDto;
import com.bookstore.book.entity.BookEntity;
import com.bookstore.book.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock private BookRepository bookRepository;

  @InjectMocks private BookService bookService;

  private BookEntity bookEntity;
  private UUID bookId;

  @BeforeEach
  void setUp() {
    bookId = UUID.randomUUID();
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

    assertThrows(ResponseStatusException.class, () -> bookService.getBookById(UUID.randomUUID()));
  }
}
