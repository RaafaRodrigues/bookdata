package com.br.bookdata.domain.repository;

import com.br.bookdata.domain.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IBookRepository extends JpaRepository<Book, Long> {
  Page<Book> findByGenreIgnoreCase(String genre, Pageable pageable);

  Page<Book> findByAuthorIgnoreCase(String author, Pageable pageable);

  @Query(value = "SELECT COUNT(id) > 0 FROM book", nativeQuery = true)
  boolean thereAreRecords();
}
