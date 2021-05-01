package com.example.demo.dao;

import com.example.demo.entity.Book;

public interface BookCrudDao {
    Book createBook(Book book);

    Book readBook(String bookId);

    Book updateBook(Book book);

    void deleteBook(String bookId);
}
