package com.bookstore.user.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bookstore.clients.book.BookClient;
import com.bookstore.clients.book.dto.BookDto;
import com.bookstore.user.cart.CartService;
import com.bookstore.user.cart.dto.AddBookToCartRequestDto;
import com.bookstore.user.cart.dto.AddBookToCartResponseDto;
import com.bookstore.user.cart.dto.CartDto;
import com.bookstore.user.cart.entity.CartEntity;
import com.bookstore.user.cart.entity.CartItemEntity;
import com.bookstore.user.cart.enums.CartStatus;
import com.bookstore.user.cart.repository.CartRepository;
import com.bookstore.user.entity.UserEntity;
import com.bookstore.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private CartRepository cartRepository;
  @Mock private BookClient bookClient;
  @InjectMocks private CartService cartService;

  private UserEntity user;
  private CartEntity cart;
  private BookDto bookDto;
  private UUID bookId;

  @BeforeEach
  void setUp() {
    bookId = UUID.randomUUID();
    user = UserEntity.builder().id(UUID.randomUUID()).email("test@example.com").build();
    bookDto =
        BookDto.builder()
            .bookId(bookId)
            .title("Test Book")
            .price(new BigDecimal("10.0"))
            .quantityInStock(5)
            .build();
    cart =
        CartEntity.builder()
            .id(1L)
            .user(user)
            .cartItems(new ArrayList<>())
            .status(CartStatus.PENDING)
            .build();
  }

  @Test
  void getCartById_ShouldReturnCartDto() {
    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

    CartDto result = cartService.getCartById(1L);

    assertThat(result).isNotNull();
    assertThat(result.getCartId()).isEqualTo(cart.getId());
    verify(cartRepository).findById(1L);
  }

  @Test
  void getCartById_WhenCartNotFound_ShouldThrowException() {
    when(cartRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> cartService.getCartById(1L))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("No cart found with id");
  }

  @Test
  void addBookToCart_ShouldAddBook() {
    lenient().when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    lenient()
        .when(cartRepository.findByUser_EmailAndStatus(user.getEmail(), CartStatus.PENDING))
        .thenReturn(Optional.of(cart));
    lenient().when(bookClient.getBookById(bookId)).thenReturn(bookDto);

    AddBookToCartRequestDto requestDto =
        AddBookToCartRequestDto.builder().bookId(bookId).quantity(2).build();

    AddBookToCartResponseDto response = cartService.addBookToCart(user.getEmail(), requestDto);

    assertThat(response).isNotNull();
    assertThat(response.getCartId()).isEqualTo(cart.getId());
    assertThat(cart.getCartItems()).hasSize(1);

    verify(bookClient).getBookById(bookId);
  }

  @Test
  void removeBookFromCart_ShouldRemoveBook() {
    CartItemEntity cartItem =
        CartItemEntity.builder()
            .bookId(bookId)
            .title("Test Book")
            .quantity(2)
            .pricePerUnit(new BigDecimal("10.0"))
            .build();
    cart.getCartItems().add(cartItem);

    when(cartRepository.findByUser_EmailAndStatus(user.getEmail(), CartStatus.PENDING))
        .thenReturn(Optional.of(cart));

    cartService.removeBookFromCart(user.getEmail(), bookId);

    assertThat(cart.getCartItems()).isEmpty();
  }
}
