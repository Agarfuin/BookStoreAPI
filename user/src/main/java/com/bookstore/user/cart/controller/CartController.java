package com.bookstore.user.cart.controller;

import com.bookstore.user.cart.CartService;
import com.bookstore.user.cart.dto.AddBookToCartRequestDto;
import com.bookstore.user.cart.dto.AddBookToCartResponseDto;
import com.bookstore.user.cart.dto.CartDto;
import com.bookstore.user.cart.dto.CheckoutResponseDto;
import com.bookstore.user.cart.enums.PaymentMethod;
import jakarta.validation.Valid;
import java.util.List;
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

  @PatchMapping("/{bookId}")
  public ResponseEntity<String> updateBookInCart(
      @RequestHeader("X-User-Email") String email,
      @PathVariable("bookId") UUID bookId,
      @RequestParam("quantity") Integer quantity) {
    cartService.updateBookInCart(email, bookId, quantity);
    return ResponseEntity.status(HttpStatus.OK)
        .body(String.format("Updated book with ID: %s", bookId));
  }

  @DeleteMapping
  public ResponseEntity<String> clearCart(@RequestHeader("X-User-Email") String email) {
    cartService.clearCart(email);
    return ResponseEntity.status(HttpStatus.OK).body("Cart successfully cleared");
  }

  @DeleteMapping("/{bookId}")
  public ResponseEntity<String> removeBookFromCart(
      @RequestHeader("X-User-Email") String email, @PathVariable("bookId") UUID bookId) {
    cartService.removeBookFromCart(email, bookId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(String.format("Removed book with ID: %s", bookId));
  }

  @PostMapping("/checkout")
  public ResponseEntity<CheckoutResponseDto> checkout(
      @RequestHeader("X-User-Email") String email,
      @RequestParam("address") String address,
      @RequestParam("paymentMethod") PaymentMethod paymentMethod) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(cartService.checkout(email, address, paymentMethod));
  }

  @GetMapping("/history")
  public ResponseEntity<List<CartDto>> getOrderHistory(
      @RequestHeader("X-User-Email") String email) {
    return ResponseEntity.status(HttpStatus.OK).body(cartService.getPurchaseHistory(email));
  }
}
