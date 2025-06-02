package com.bookstore.user.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bookstore.user.UserService;
import com.bookstore.user.dto.LoginRequestDto;
import com.bookstore.user.dto.SignupRequestDto;
import com.bookstore.user.dto.SignupResponseDto;
import com.bookstore.user.dto.ValidatedUserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  private MockMvc mockMvc;

  @Mock private UserService userService;

  @InjectMocks private UserController userController;

  @InjectMocks private UserAdminController userAdminController;

  private ObjectMapper objectMapper;
  private SignupRequestDto signupRequestDto;
  private ValidatedUserDto validatedUserDto;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(userController, userAdminController).build();
    objectMapper = new ObjectMapper();
    signupRequestDto = new SignupRequestDto("John", "Doe", "john.doe@example.com", "password123");
    validatedUserDto =
        new ValidatedUserDto(
            UUID.fromString("d0e2b1c8-41be-4e5b-9743-2ab706410032"), // Test User ID
            "john.doe@example.com",
            "John",
            "Doe",
            "USER",
            Boolean.TRUE,
            null);
  }

  @Test
  void helloWorld_ShouldReturnHelloWorld() throws Exception {
    mockMvc
        .perform(get("/api/v1/users/hello-world"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello World"));
  }

  @Test
  void getUserDetails_ShouldReturnUserDetails() throws Exception {
    when(userService.getUserDetails(validatedUserDto.getEmail())).thenReturn(validatedUserDto);

    mockMvc
        .perform(get("/api/v1/users/user").header("X-User-Email", validatedUserDto.getEmail()))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(validatedUserDto)));
  }

  @Test
  void createUser_ShouldReturnCreatedUser() throws Exception {
    SignupResponseDto responseDto =
        new SignupResponseDto(
            validatedUserDto.getId(), "John", "john.doe@example.com", "confirmationToken");
    when(userService.createUser(any(SignupRequestDto.class))).thenReturn(responseDto);

    mockMvc
        .perform(
            post("/api/v1/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequestDto)))
        .andExpect(status().isCreated())
        .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
  }

  @Test
  void validateCredentials_ShouldReturnValidatedUser() throws Exception {
    LoginRequestDto loginRequestDto = new LoginRequestDto("john.doe@example.com", "password123");
    when(userService.validateCredentials(loginRequestDto.getEmail(), loginRequestDto.getPassword()))
        .thenReturn(validatedUserDto);

    mockMvc
        .perform(
            post("/api/v1/admin/users/user/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(validatedUserDto)));
  }

  @Test
  void verifyUser_ShouldReturnOk() throws Exception {
    doNothing().when(userService).verifyUser("validToken");

    mockMvc
        .perform(post("/api/v1/admin/users/user/verify").param("token", "validToken"))
        .andExpect(status().isOk());
  }

  @Test
  void getAllUsers_ShouldReturnUserList() throws Exception {
    when(userService.getAllUsers()).thenReturn(List.of(validatedUserDto));

    mockMvc
        .perform(get("/api/v1/admin/users"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(List.of(validatedUserDto))));
  }

  @Test
  void getUserById_ShouldReturnUserDetails() throws Exception {
    when(userService.getUserById(validatedUserDto.getId())).thenReturn(validatedUserDto);

    mockMvc
        .perform(get("/api/v1/admin/users/user/{userId}", validatedUserDto.getId()))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(validatedUserDto)));
  }
}
