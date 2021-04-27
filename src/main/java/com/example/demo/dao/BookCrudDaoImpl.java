package com.example.demo.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.example.demo.model.Book;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class BookCrudDaoImpl implements BookCrudDao {

    private final DynamoDBMapper dynamoDBMapper;

    @Override
    public Book createBook(Book book) {
        dynamoDBMapper.save(book);
        return book;
    }

    @Override
    public Book readBook(String bookId) {
        return dynamoDBMapper.load(Book.class, bookId);
    }

    @Override
    public Book updateBook(Book book) {
        Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
        expectedAttributeValueMap.put("ISBN", new ExpectedAttributeValue(new AttributeValue().withS(book.getIsbn())));
        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression().withExpected(expectedAttributeValueMap);
        dynamoDBMapper.save(book, saveExpression);
        return book;
    }

    @Override
    public void deleteBook(String bookId) {
        Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
        expectedAttributeValueMap.put("ISBN", new ExpectedAttributeValue(new AttributeValue().withS(bookId)));
        DynamoDBDeleteExpression deleteExpression = new DynamoDBDeleteExpression().withExpected(expectedAttributeValueMap);
        Book book = Book.builder()
                .isbn(bookId)
                .build();
        dynamoDBMapper.delete(book, deleteExpression);
    }
}
