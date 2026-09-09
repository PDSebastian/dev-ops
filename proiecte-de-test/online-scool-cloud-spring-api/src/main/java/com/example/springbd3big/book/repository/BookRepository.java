package com.example.springbd3big.book.repository;

import com.example.springbd3big.book.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Override
    @EntityGraph(attributePaths = {"student"})
    List<Book> findAll();

    @Override
    @EntityGraph(attributePaths = {"student"})
    Optional<Book> findById(Long bookId);

    @EntityGraph(attributePaths = {"student"})
    List<Book> findByStudentId(Long studentId);

    @EntityGraph(attributePaths = {"student"})
    List<Book> findByBookNameContainingIgnoreCase(String keyword);
}
