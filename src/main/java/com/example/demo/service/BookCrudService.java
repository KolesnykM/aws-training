package com.example.demo.service;

import com.example.demo.model.Book;

public interface BookCrudService {
    Book createBook(Book book);

    Book readBook(String bookId);

    Book updateBook(Book book);

    void deleteBook(String bookId);
}
