package com.example.demo.service;

import com.example.demo.dao.BookCrudDao;
import com.example.demo.dto.BookDto;
import com.example.demo.mapper.BooksMapperImpl;
import com.example.demo.entity.Book;
import com.example.demo.message.BookMessageBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class BookCrudServiceImpl implements BookCrudService {
    private final QueueMessagingTemplate queueMessagingTemplate;
    @Value("${cloud.aws.end-point.uri}")
    private String endpoint;
    private final BookCrudDao bookCrudDao;
    private final BooksMapperImpl booksMapper;

    @Override
    public Book createBook(BookDto bookDto) {
        final Book book = booksMapper.bookDtoToBook(bookDto);
        final Book result = bookCrudDao.createBook(book);
        log.info("Create book {}", result);
        BookMessageBuilder message = BookMessageBuilder.builder()
                .book(result.toString())
                .message("has been create")
                .time(LocalDateTime.now().toString())
                .build();
        sendMessageToSQS(message);
        return result;
    }

    @Override
    public Book readBook(String bookId) {
        final Book book = bookCrudDao.readBook(bookId);
        log.info("Read book {}", book);
        BookMessageBuilder message = BookMessageBuilder.builder()
                .book(book.toString())
                .message("has been read with id" + bookId)
                .time(LocalDateTime.now().toString())
                .build();
        sendMessageToSQS(message);
        return book;
    }

    @Override
    public Book updateBook(BookDto bookDto) {
        final Book book = booksMapper.bookDtoToBook(bookDto);
        final Book result = bookCrudDao.updateBook(book);
        log.info("Update book {} on {}", book, result);
        BookMessageBuilder message = BookMessageBuilder.builder()
                .book(book.toString())
                .message("has been update on " + result)
                .time(LocalDateTime.now().toString())
                .build();
        sendMessageToSQS(message);
        return result;
    }

    @Override
    public void deleteBook(String bookId) {
        bookCrudDao.deleteBook(bookId);
        log.info("Delete book with id {}", bookId);
        BookMessageBuilder message = BookMessageBuilder.builder()
                .message("The book with id" + bookId + "has been delete")
                .time(LocalDateTime.now().toString())
                .build();
        sendMessageToSQS(message);
    }

    @SneakyThrows
    private void sendMessageToSQS(BookMessageBuilder message) {
        ObjectMapper objectMapper = new ObjectMapper();
        String messageAsString = objectMapper.writeValueAsString(message);
        log.debug("Send message {}", messageAsString);
        queueMessagingTemplate.send(endpoint, MessageBuilder.withPayload(messageAsString).build());
    }

}