package com.example.demo.mapper;

import com.example.demo.dto.BookDto;
import com.example.demo.entity.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BooksMapper {
    BookDto bookToBookDto(Book book);
    Book bookDtoToBook(BookDto dto);
}
