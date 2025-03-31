package com.bookstore.book.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.book.BookService;
import com.bookstore.book.dto.*;
import com.bookstore.book.enums.Genre;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

  @Mock private BookService bookService;
  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private BookDto bookDto;
  private UUID bookId;

  @BeforeEach
  void setUp() {
    BookController bookController = new BookController(bookService);
    BookAdminController bookAdminController = new BookAdminController(bookService);
    mockMvc = MockMvcBuilders.standaloneSetup(bookController, bookAdminController).build();
    objectMapper = new ObjectMapper();

    bookId = UUID.randomUUID();
    bookDto =
        BookDto.builder()
            .bookId(bookId)
            .title("Test Book")
            .author("Test Author")
            .genre(Set.of(Genre.FICTION))
            .price(new BigDecimal("29.99"))
            .build();
  }

  @Test
  void getAllBooks_ShouldReturnBookList() throws Exception {
    when(bookService.getAllBooks()).thenReturn(List.of(bookDto));

    mockMvc
        .perform(get("/api/v1/books").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getBookById_ShouldReturnBookDetails() throws Exception {
    when(bookService.getBookById(bookId)).thenReturn(bookDto);

    mockMvc
        .perform(get("/api/v1/books/" + bookId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void createBook_ShouldCreatedBookId() throws Exception {
    CreateBookRequestDto request =
        CreateBookRequestDto.builder()
            .title("New Book")
            .author("New Author")
            .publicationYear(2024) // Added required field
            .genre(Set.of(Genre.FICTION))
            .price(new BigDecimal("19.99"))
            .quantity(10)
            .build();

    CreateBookResponseDto response = new CreateBookResponseDto(UUID.randomUUID());
    when(bookService.addBook(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/admin/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  void updateBookById_ShouldUpdateBook() throws Exception {
    UpdateBookRequestDto request =
        UpdateBookRequestDto.builder()
            .title("Updated Title")
            .price(new BigDecimal("39.99"))
            .build();

    when(bookService.updateBookById(any(), any())).thenReturn(bookDto);

    mockMvc
        .perform(
            patch("/api/v1/admin/books/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void deleteBookById_ShouldDeleteBook() throws Exception {
    mockMvc
        .perform(delete("/api/v1/admin/books/" + bookId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
