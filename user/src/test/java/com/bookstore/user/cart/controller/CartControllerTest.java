package com.bookstore.user.cart.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bookstore.user.cart.CartService;
import com.bookstore.user.cart.dto.AddItemToCartRequestDto;
import com.bookstore.user.cart.dto.AddItemToCartResponseDto;
import com.bookstore.user.cart.dto.CartDto;
import com.bookstore.user.cart.enums.CartStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CartControllerTest {

  private MockMvc mockMvc;

  @Mock private CartService cartService;

  @InjectMocks private CartController cartController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
  }

  @Test
  void getCartDetails_ShouldReturnCart() throws Exception {
    String email = "user@example.com";
    CartDto cartDto =
        CartDto.builder().cartId(1L).userId(UUID.randomUUID()).status(CartStatus.PENDING).build();

    when(cartService.getCartDetails(email)).thenReturn(cartDto);

    mockMvc
        .perform(get("/api/v1/users/cart").header("X-User-Email", email))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cartId").value(cartDto.getCartId()));
  }

  @Test
  void addItemToCart_ShouldReturnCreatedCart() throws Exception {
    String email = "user@example.com";
    AddItemToCartRequestDto requestDto = new AddItemToCartRequestDto(UUID.randomUUID(), 2);
    AddItemToCartResponseDto responseDto = AddItemToCartResponseDto.builder().cartId(1L).build();

    when(cartService.addItemToCart(eq(email), any(AddItemToCartRequestDto.class)))
        .thenReturn(responseDto);

    mockMvc
        .perform(
            post("/api/v1/users/cart")
                .header("X-User-Email", email)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.cartId").value(responseDto.getCartId()));
  }

  @Test
  void updateItemInCartById_ShouldReturnSuccessMessage() throws Exception {
    String email = "user@example.com";
    UUID itemId = UUID.randomUUID();
    int quantity = 5;
    String successMessage = "Updated item with ID: " + itemId;

    when(cartService.updateItemInCartById(email, itemId, quantity)).thenReturn(successMessage);

    mockMvc
        .perform(
            patch("/api/v1/users/cart/{itemId}", itemId)
                .header("X-User-Email", email)
                .param("quantity", String.valueOf(quantity)))
        .andExpect(status().isOk())
        .andExpect(content().string("Updated item with ID: " + itemId));
  }

  @Test
  void clearCart_ShouldReturnSuccessMessage() throws Exception {
    String email = "user@example.com";
    String successMessage = "Cart successfully cleared";

    when(cartService.clearCart(email)).thenReturn(successMessage);

    mockMvc
        .perform(delete("/api/v1/users/cart").header("X-User-Email", email))
        .andExpect(status().isOk())
        .andExpect(content().string("Cart successfully cleared"));
  }

  @Test
  void removeItemFromCartById_ShouldReturnSuccessMessage() throws Exception {
    String email = "user@example.com";
    UUID itemId = UUID.randomUUID();
    String successMessage = "Removed item with ID: " + itemId;

    when(cartService.removeItemFromCartById(email, itemId)).thenReturn(successMessage);

    mockMvc
        .perform(delete("/api/v1/users/cart/{itemId}", itemId).header("X-User-Email", email))
        .andExpect(status().isOk())
        .andExpect(content().string("Removed item with ID: " + itemId));
  }
}
