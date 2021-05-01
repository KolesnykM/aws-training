package com.example.demo.service;

import com.example.demo.dto.BookDto;
import com.example.demo.entity.Book;

public interface BookCrudService {
    Book createBook(BookDto bookDto);

    Book readBook(String bookId);

    Book updateBook(BookDto bookDto);

    void deleteBook(String bookId);
}
