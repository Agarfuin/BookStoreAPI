package com.bookstore.user.cart.controller;

import com.bookstore.clients.book.dto.BookDto;
import com.bookstore.user.cart.CartService;
import com.bookstore.user.cart.dto.AddBookToCartRequestDto;
import com.bookstore.user.cart.dto.AddBookToCartResponseDto;
import com.bookstore.user.cart.dto.CartDto;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/cart")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  @GetMapping
  public ResponseEntity<CartDto> getCartDetails(@RequestHeader("X-User-Email") String email) {
    return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartDetails(email));
  }

  @PostMapping
  public ResponseEntity<AddBookToCartResponseDto> addBookToCart(
      @RequestHeader("X-User-Email") String email,
      @Valid @RequestBody AddBookToCartRequestDto addBookToCartRequestDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(cartService.addBookToCart(email, addBookToCartRequestDto));
  }

  @PatchMapping
  public String updateBookInCart(
      @RequestHeader("X-User-Email") String email, @RequestBody BookDto bookDto) {
    return "Updated book in cart for user with ID: ";
  }

  @DeleteMapping
  public String deleteCart(@RequestHeader("X-User-Email") String email) {
    return "Removed book from cart for user with ID: ";
  }

  @DeleteMapping("/{bookId}")
  public String removeBookFromCart(
      @RequestHeader("X-User-Email") String email,
      @PathVariable UUID userId,
      @PathVariable UUID bookId) {
    return "Removed book with ID: " + bookId + " from cart for user with ID: " + userId;
  }
}
