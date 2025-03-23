package com.bookstore.book.dto;

import com.bookstore.book.enums.Genre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookRequestDto {

  @NotBlank(message = "Title is required")
  private String title;

  @NotBlank(message = "Author is required")
  private String author;

  @NotNull(message = "Publication year is required")
  private Integer publicationYear;

  @NotEmpty(message = "At least one genre must be specified")
  private Set<Genre> genre;

  private String description;

  @NotNull(message = "Price is required")
  private BigDecimal price;

  @NotNull(message = "Quantity is required")
  @Min(value = 0, message = "Quantity cannot be negative")
  private Integer quantity;
}
