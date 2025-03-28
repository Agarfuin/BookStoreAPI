package com.bookstore.user.cart.controller;

import com.bookstore.user.cart.CartService;
import com.bookstore.user.cart.dto.CartDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users/cart")
@RequiredArgsConstructor
public class CartAdminController {

  private final CartService cartService;

  @GetMapping
  public ResponseEntity<List<CartDto>> getAllCarts() {
    return ResponseEntity.status(HttpStatus.OK).body(cartService.getAllCarts());
  }

  @GetMapping("/{cartId}")
  public ResponseEntity<CartDto> getCartById(@PathVariable("cartId") Long cartId) {
    return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartById(cartId));
  }

  @GetMapping("/history")
  public ResponseEntity<List<CartDto>> getCartHistory() {
    return ResponseEntity.status(HttpStatus.OK).body(cartService.getAllPurchaseHistory());
  }
}
