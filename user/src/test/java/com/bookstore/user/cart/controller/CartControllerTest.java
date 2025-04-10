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
  private static UUID itemId;

  @BeforeEach
  void setUp() {
    itemId = UUID.fromString("df0270a6-b253-4f63-8a1b-ae721100bccd"); // Test Item ID
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
  }

  @Test
  void getCartDetails_ShouldReturnCart() throws Exception {
    String email = "user@example.com";
    CartDto cartDto =
        CartDto.builder()
            .cartId(1L)
            .userId(UUID.fromString("d0e2b1c8-41be-4e5b-9743-2ab706410032")) // Test User ID
            .status(CartStatus.PENDING)
            .build();

    when(cartService.getCartDetails(email)).thenReturn(cartDto);

    mockMvc
        .perform(get("/api/v1/users/cart").header("X-User-Email", email))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(cartDto)));
  }

  @Test
  void addItemToCart_ShouldReturnCreatedCart() throws Exception {
    String email = "user@example.com";
    AddItemToCartRequestDto requestDto =
        AddItemToCartRequestDto.builder().itemId(itemId).quantity(2).build();
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
        .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
  }

  @Test
  void updateItemInCartById_ShouldReturnSuccessMessage() throws Exception {
    String email = "user@example.com";
    int quantity = 5;
    String successMessage = "Updated item with ID: " + itemId;

    when(cartService.updateItemInCartById(email, itemId, quantity)).thenReturn(successMessage);

    mockMvc
        .perform(
            patch("/api/v1/users/cart/{itemId}", itemId)
                .header("X-User-Email", email)
                .param("quantity", String.valueOf(quantity)))
        .andExpect(status().isOk())
        .andExpect(content().string(successMessage));
  }

  @Test
  void clearCart_ShouldReturnSuccessMessage() throws Exception {
    String email = "user@example.com";
    String successMessage = "Cart successfully cleared";

    when(cartService.clearCart(email)).thenReturn(successMessage);

    mockMvc
        .perform(delete("/api/v1/users/cart").header("X-User-Email", email))
        .andExpect(status().isOk())
        .andExpect(content().string(successMessage));
  }

  @Test
  void removeItemFromCartById_ShouldReturnSuccessMessage() throws Exception {
    String email = "user@example.com";
    String successMessage = "Removed item with ID: " + itemId;

    when(cartService.removeItemFromCartById(email, itemId)).thenReturn(successMessage);

    mockMvc
        .perform(delete("/api/v1/users/cart/{itemId}", itemId).header("X-User-Email", email))
        .andExpect(status().isOk())
        .andExpect(content().string(successMessage));
  }
}
