package com.bookstore.user;

import com.bookstore.user.dto.CreateUserRequestDto;
import com.bookstore.user.dto.CreateUserResponseDto;
import com.bookstore.user.entity.UserEntity;
import com.bookstore.user.enums.Role;
import com.bookstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public CreateUserResponseDto createUser(CreateUserRequestDto createUserRequestDto) {
    UserEntity userEntity =
        UserEntity.builder()
            .firstName(createUserRequestDto.getFirstName())
            .lastName(createUserRequestDto.getLastName())
            .email(createUserRequestDto.getEmail())
            .password(createUserRequestDto.getPassword())
            .role(Role.USER)
            .build();

    userRepository.saveAndFlush(userEntity);
    return CreateUserResponseDto.builder()
        .id(userEntity.getId())
        .email(userEntity.getEmail())
        .build();
  }
}
