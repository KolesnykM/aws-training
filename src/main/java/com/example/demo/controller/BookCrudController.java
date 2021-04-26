package com.example.demo.controller;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.example.demo.model.Book;
import com.example.demo.service.BookCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/books")
public class BookCrudController {
    private static final String CREATE = "{\"book\":\"%s\", \"message\":\"has been create\", \"time\":\"%s\"}";
    private static final String UPDATE = "{\"book\":\"%s\", \", \"message\":\"has been update on %s\", \"time\":\"%s\"}";
    private static final String READ = "{\"book\":\"%s\", \", \"message\":\"has been read with id %s\", \"time\":\"%s\"}";
    private static final String DELETE = "{\"message\":\"The book with id %s has been delete\", \"time\":\"%s\"}";
    @Autowired
    private BookCrudService bookCrudService;
    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;
    @Value("${cloud.aws.end-point.uri}")
    private String endpoint;

    @PostMapping
    public ResponseEntity createBook(@RequestBody Book book) {
        try {
            Book response = bookCrudService.createBook(book);
            sendMessageToSQS(String.format(CREATE, response, LocalDateTime.now()));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (AmazonServiceException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode()), e.getMessage(), e);
        } catch (AmazonClientException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @GetMapping("/{bookId}")
    public ResponseEntity readBook(@PathVariable String bookId) {
        try {
            Book response = bookCrudService.readBook(bookId);
            sendMessageToSQS(String.format(READ, response, bookId, LocalDateTime.now()));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (AmazonServiceException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode()), e.getMessage(), e);
        } catch (AmazonClientException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @PutMapping
    public ResponseEntity updateBook(@RequestBody Book book) {
        try {
            Book response = bookCrudService.updateBook(book);
            sendMessageToSQS(String.format(UPDATE, book, response, LocalDateTime.now()));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (AmazonServiceException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode()), e.getMessage(), e);
        } catch (AmazonClientException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity deleteBook(@PathVariable String bookId) {
        try {
            bookCrudService.deleteBook(bookId);
            sendMessageToSQS(String.format(DELETE, bookId, LocalDateTime.now()));
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (AmazonServiceException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode()), e.getMessage(), e);
        } catch (AmazonClientException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    private void sendMessageToSQS(String message) {
        queueMessagingTemplate.send(endpoint, MessageBuilder.withPayload(message).build());
    }
}