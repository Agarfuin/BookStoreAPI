package com.bookstore.user.cart;

import com.bookstore.clients.book.BookClient;
import com.bookstore.clients.book.dto.BookDto;
import com.bookstore.clients.book.dto.UpdateBookRequestDto;
import com.bookstore.user.cart.dto.AddItemToCartRequestDto;
import com.bookstore.user.cart.dto.AddItemToCartResponseDto;
import com.bookstore.user.cart.dto.CartDto;
import com.bookstore.user.cart.dto.CheckoutResponseDto;
import com.bookstore.user.cart.entity.CartEntity;
import com.bookstore.user.cart.entity.CartItemEntity;
import com.bookstore.user.cart.enums.CartStatus;
import com.bookstore.user.cart.enums.PaymentMethod;
import com.bookstore.user.cart.repository.CartRepository;
import com.bookstore.user.entity.UserEntity;
import com.bookstore.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.*;
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

  public AddItemToCartResponseDto addItemToCart(
      String email, AddItemToCartRequestDto addBookToCartRequestDto) {
    CartEntity cart = getCurrentUsersCartCreateNewOneIfNotExists(email);

    BookDto bookToAdd = bookClient.getBookById(addBookToCartRequestDto.getItemId());

    cart.getCartItems()
        .add(
            CartItemEntity.builder()
                .itemId(bookToAdd.getBookId())
                .title(bookToAdd.getTitle())
                .quantity(addBookToCartRequestDto.getQuantity())
                .pricePerUnit(bookToAdd.getPrice())
                .build());
    cartRepository.save(cart);

    return AddItemToCartResponseDto.builder().cartId(cart.getId()).build();
  }

  public String updateItemInCartById(String email, UUID itemId, Integer quantity) {
    CartEntity cart = getCurrentUsersCartCreateNewOneIfNotExists(email);

    CartItemEntity bookToUpdate =
        cart.getCartItems().stream()
            .filter(item -> item.getItemId().equals(itemId))
            .findFirst()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Item with id %s not found in the cart", itemId)));

    if (quantity <= 0) {
      cart.getCartItems().remove(bookToUpdate);
    } else {
      bookToUpdate.setQuantity(quantity);
    }

    cartRepository.save(cart);

    return String.format("Updated item with ID: %s", itemId);
  }

  public String removeItemFromCartById(String email, UUID itemId) {
    CartEntity cart = getCurrentUsersCartCreateNewOneIfNotExists(email);

    CartItemEntity bookToRemove =
        cart.getCartItems().stream()
            .filter(item -> item.getItemId().equals(itemId))
            .findFirst()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Item with id %s not found in the cart", itemId)));

    cart.getCartItems().remove(bookToRemove);

    cartRepository.save(cart);

    return String.format("Removed item with ID: %s", itemId);
  }

  public String clearCart(String email) {
    CartEntity cart = getCurrentUsersCartCreateNewOneIfNotExists(email);
    cart.getCartItems().clear();
    cartRepository.save(cart);

    return "Cart successfully cleared";
  }

  public CheckoutResponseDto checkout(String email, String address, PaymentMethod paymentMethod) {
    CartEntity cart = getCurrentUsersCartCreateNewOneIfNotExists(email);

    if (cart.getCartItems().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot checkout an empty cart");
    }

    Map<UUID, Integer> stockUpdates = new HashMap<>();
    List<UUID> outOfStockItems = new ArrayList<>();

    for (CartItemEntity item : cart.getCartItems()) {
      BookDto book = bookClient.getBookById(item.getItemId());
      int availableStock = book.getQuantityInStock();
      int requestedQuantity = item.getQuantity();

      if (requestedQuantity > availableStock && !outOfStockItems.contains(item.getItemId())) {
        outOfStockItems.add(item.getItemId());
      }
      stockUpdates.merge(
          book.getBookId(),
          availableStock - requestedQuantity,
          (currentValue, newValue) -> {
            if (currentValue - requestedQuantity < 0
                && !outOfStockItems.contains(item.getItemId())) {
              outOfStockItems.add(item.getItemId());
            }
            return currentValue - requestedQuantity;
          });
    }

    if (!outOfStockItems.isEmpty()) {
      String outOfStockBookIds =
          outOfStockItems.stream()
              .map(UUID::toString)
              .reduce((id1, id2) -> id1 + ", " + id2)
              .orElse("");
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          String.format("Items with IDs %s are out of stock", outOfStockBookIds));
    }

    stockUpdates.forEach(
        (bookId, newQuantity) ->
            bookClient.updateBookById(
                bookId, UpdateBookRequestDto.builder().quantity(newQuantity).build()));

    cart.setStatus(CartStatus.CHECKED_OUT);
    cartRepository.save(cart);

    return CheckoutResponseDto.builder()
        .checkoutDate(LocalDateTime.now())
        .cartId(cart.getId())
        .address(address)
        .totalPrice(cart.getTotalPrice())
        .paymentMethod(paymentMethod)
        .build();
  }

  public List<CartDto> getPurchaseHistory(String email) {
    List<CartEntity> carts =
        cartRepository
            .findByUser_Email(email)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("No carts found for user with email %s", email)));

    return carts.stream()
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

  public List<CartDto> getAllPurchaseHistory() {
    List<CartEntity> carts = cartRepository.findAll();

    return carts.stream()
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

  private CartEntity getCurrentUsersCartCreateNewOneIfNotExists(String email) {
    return cartRepository
        .findByUser_EmailAndStatus(email, CartStatus.PENDING)
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
