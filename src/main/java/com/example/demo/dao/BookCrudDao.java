package com.example.demo.dao;

import com.example.demo.model.Book;

public interface BookCrudDao {
    Book createBook(Book book);

    Book readBook(String bookId);

    Book updateBook(Book book);

    void deleteBook(String bookId);
}
