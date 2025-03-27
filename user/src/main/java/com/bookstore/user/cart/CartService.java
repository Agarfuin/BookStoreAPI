package com.bookstore.user.cart;

import com.bookstore.clients.book.BookClient;
import com.bookstore.clients.book.dto.BookDto;
import com.bookstore.user.cart.dto.AddBookToCartRequestDto;
import com.bookstore.user.cart.dto.AddBookToCartResponseDto;
import com.bookstore.user.cart.dto.CartDto;
import com.bookstore.user.cart.entity.CartEntity;
import com.bookstore.user.cart.entity.CartItemEntity;
import com.bookstore.user.cart.enums.CartStatus;
import com.bookstore.user.cart.repository.CartRepository;
import com.bookstore.user.entity.UserEntity;
import com.bookstore.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CartService {

  private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final BookClient bookClient;

  public List<CartDto> getAllCarts() {
    return cartRepository.findAll().stream()
        .map(
            cart ->
                CartDto.builder()
                    .cartId(cart.getId())
                    .userId(cart.getUser().getId())
                    .cartItems(cart.getCartItems())
                    .totalPrice(cart.getTotalPrice())
                    .status(cart.getStatus())
                    .build())
        .toList();
  }

  public CartDto getCartById(Long cartId) {
    CartEntity cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("No cart found with id %s", cartId)));

    return CartDto.builder()
        .cartId(cart.getId())
        .userId(cart.getUser().getId())
        .cartItems(cart.getCartItems())
        .totalPrice(cart.getTotalPrice())
        .status(cart.getStatus())
        .build();
  }

  public CartDto getCartDetails(String email) {
    CartEntity cart = getCurrentUsersCartCreateNewOneIfNotExists(email);

    return CartDto.builder()
        .cartId(cart.getId())
        .userId(cart.getUser().getId())
        .cartItems(cart.getCartItems())
        .totalPrice(cart.getTotalPrice())
        .status(CartStatus.PENDING)
        .build();
  }

  public AddBookToCartResponseDto addBookToCart(
      String email, AddBookToCartRequestDto addBookToCartRequestDto) {
    CartEntity cart = getCurrentUsersCartCreateNewOneIfNotExists(email);

    BookDto bookToAdd = bookClient.getBookById(addBookToCartRequestDto.getBookId());

    cart.getCartItems()
        .add(
            CartItemEntity.builder()
                .bookId(bookToAdd.getBookId())
                .title(bookToAdd.getTitle())
                .quantity(addBookToCartRequestDto.getQuantity())
                .pricePerUnit(bookToAdd.getPrice())
                .build());
    cartRepository.save(cart);

    return AddBookToCartResponseDto.builder().cartId(cart.getId()).build();
  }

  private CartEntity getCurrentUsersCartCreateNewOneIfNotExists(String email) {
    return cartRepository
        .findByUser_Email(email)
        .orElseGet(
            () -> {
              UserEntity currentUser =
                  userRepository
                      .findByEmail(email)
                      .orElseThrow(
                          () ->
                              new ResponseStatusException(
                                  HttpStatus.INTERNAL_SERVER_ERROR,
                                  String.format("No user found with email: %s", email)));

              CartEntity newCart =
                  CartEntity.builder()
                      .user(currentUser)
                      .cartItems(new ArrayList<>())
                      .status(CartStatus.PENDING)
                      .build();
              return cartRepository.save(newCart);
            });
  }
}
