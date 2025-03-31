package com.bookstore.user.cart.controller;

import com.bookstore.user.cart.CartService;
import com.bookstore.user.cart.dto.AddItemToCartRequestDto;
import com.bookstore.user.cart.dto.AddItemToCartResponseDto;
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
  public ResponseEntity<AddItemToCartResponseDto> addItemToCart(
      @RequestHeader("X-User-Email") String email,
      @Valid @RequestBody AddItemToCartRequestDto addBookToCartRequestDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(cartService.addItemToCart(email, addBookToCartRequestDto));
  }

  @PatchMapping("/{itemId}")
  public ResponseEntity<String> updateItemInCart(
      @RequestHeader("X-User-Email") String email,
      @PathVariable("itemId") UUID itemId,
      @RequestParam("quantity") Integer quantity) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(cartService.updateItemInCartById(email, itemId, quantity));
  }

  @DeleteMapping
  public ResponseEntity<String> clearCart(@RequestHeader("X-User-Email") String email) {
    return ResponseEntity.status(HttpStatus.OK).body(cartService.clearCart(email));
  }

  @DeleteMapping("/{itemId}")
  public ResponseEntity<String> removeItemFromCart(
      @RequestHeader("X-User-Email") String email, @PathVariable("itemId") UUID itemId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(cartService.removeItemFromCartById(email, itemId));
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
