package com.br.bookdata.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "book",
    indexes = {
      @Index(name = "idx_genre", columnList = "genre"),
      @Index(name = "idx_author", columnList = "author")
    })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String genre;
  private String author;

  @Column(length = 2000)
  private String description;
}
