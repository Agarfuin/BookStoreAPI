package com.bookstore.book.entity;

import com.bookstore.book.enums.Genre;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class BookEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String author;

  private String description;

  @Column(nullable = false)
  private Integer publicationYear;

  @ElementCollection(targetClass = Genre.class)
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
  @Column(nullable = false)
  private Set<Genre> genre;

  @Column(nullable = false)
  @Digits(integer = 3, fraction = 2)
  private BigDecimal price;

  @Column(nullable = false)
  @Min(value = 0)
  private Integer quantityInStock;
}
