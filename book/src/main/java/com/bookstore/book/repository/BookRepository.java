package com.bookstore.book.repository;

import com.bookstore.book.entity.BookEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

@Validated
public interface BookRepository extends JpaRepository<BookEntity, UUID> {

  Optional<BookEntity> findById(UUID bookId);
}
