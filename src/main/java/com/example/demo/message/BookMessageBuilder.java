package com.example.demo.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookMessageBuilder {
    private String book;
    private String message;
    private String time;
}
