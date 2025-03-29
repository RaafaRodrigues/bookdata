package com.br.bookdata.domain;

import com.br.bookdata.domain.model.Book;
import com.br.bookdata.domain.repository.IBookRepository;
import com.github.javafaker.Faker;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
  @Value("${quantities.fake.books:10}")
  private Long quantityFakeBooks;

  private final IBookRepository repository;

  public DataLoader(IBookRepository repository) {
    this.repository = repository;
  }

  @Override
  public void run(String... args) {
    if (repository.thereAreRecords()) return;
    Faker faker = new Faker();
    var books =
        Stream.generate(
                () ->
                    Book.builder()
                        .title(faker.book().title())
                        .author(faker.book().author())
                        .genre(faker.book().genre())
                        .description(faker.lorem().paragraph())
                        .build())
            .limit(quantityFakeBooks)
            .toList();

    repository.saveAll(books);
  }
}
