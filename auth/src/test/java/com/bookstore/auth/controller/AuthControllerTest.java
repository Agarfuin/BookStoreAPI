package com.bookstore.auth.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bookstore.auth.AuthController;
import com.bookstore.auth.AuthService;
import com.bookstore.auth.dto.AuthResponseDto;
import com.bookstore.clients.user.dto.LoginRequestDto;
import com.bookstore.clients.user.dto.SignupRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock private AuthService authService;

  @InjectMocks private AuthController authController;

  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
  }

  @Test
  void signup_ShouldReturnAuthToken() throws Exception {
    SignupRequestDto signupRequestDto =
        new SignupRequestDto("test@example.com", "password", "John", "Doe");
    AuthResponseDto authResponseDto = new AuthResponseDto("mocked-token");
    when(authService.signup(any(SignupRequestDto.class))).thenReturn(authResponseDto);

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").value("mocked-token"));
  }

  @Test
  void login_ShouldReturnAuthToken() throws Exception {
    LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "password");
    AuthResponseDto authResponseDto = new AuthResponseDto("mocked-token");
    when(authService.login(any(LoginRequestDto.class))).thenReturn(authResponseDto);

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("mocked-token"));
  }

  @Test
  void verifyEmail_ShouldReturnSuccessMessage() throws Exception {
    String token = "mocked-token";

    mockMvc
        .perform(get("/api/v1/auth/verify").param("token", token))
        .andExpect(status().isOk())
        .andExpect(content().string("Email verified!"));
  }

  @Test
  void generateAdminToken_ShouldReturnAdminToken() throws Exception {
    AuthResponseDto authResponseDto = new AuthResponseDto("mocked-admin-token");
    when(authService.generateAdminToken()).thenReturn(authResponseDto);

    mockMvc
        .perform(get("/api/v1/auth/generate-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("mocked-admin-token"));
  }
}
