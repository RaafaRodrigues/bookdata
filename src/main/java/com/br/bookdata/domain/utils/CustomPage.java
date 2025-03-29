package com.br.bookdata.domain.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPage<T> {
  private List<T> content = new ArrayList<>();
  private int totalPages;
  private long totalElements;
  private boolean last;
  private boolean first;
  private int size;
  private int number;
  private int numberOfElements;
  private boolean empty;

  public CustomPage(Page<T> page) {
    super();
    this.totalPages = page.getTotalPages();
    this.totalElements = page.getTotalElements();
    this.last = page.isLast();
    this.first = page.isFirst();
    this.size = page.getSize();
    this.number = page.getNumber();
    this.numberOfElements = page.getNumberOfElements();
    this.empty = page.isEmpty();
    this.content = page.getContent();
  }

  public boolean isEmpty() {
    return this.content.isEmpty();
  }

  public <U> CustomPage<U> map(Function<? super T, ? extends U> converter) {
    if (isEmpty()) {
      return new CustomPage<>();
    }

    List<U> newContent = this.content.stream().map(converter).collect(Collectors.toList());

    CustomPage<U> newPage = new CustomPage<>();
    newPage.setContent(newContent);
    newPage.setTotalPages(this.totalPages);
    newPage.setTotalElements(this.totalElements);
    newPage.setLast(this.last);
    newPage.setFirst(this.first);
    newPage.setSize(this.size);
    newPage.setNumber(this.number);
    newPage.setNumberOfElements(this.numberOfElements);
    newPage.setEmpty(this.empty);

    return newPage;
  }
}
