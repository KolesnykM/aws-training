package com.example.demo.service;

import com.example.demo.dao.BookCrudDao;
import com.example.demo.model.Book;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookCrudServiceImpl implements BookCrudService {

    private final BookCrudDao bookCrudDao;

    @Override
    public Book createBook(Book book) {
        return bookCrudDao.createBook(book);
    }

    @Override
    public Book readBook(String bookId) {
        return bookCrudDao.readBook(bookId);
    }

    @Override
    public Book updateBook(Book book) {
        return bookCrudDao.updateBook(book);
    }

    @Override
    public void deleteBook(String bookId) {
        bookCrudDao.deleteBook(bookId);
    }
}