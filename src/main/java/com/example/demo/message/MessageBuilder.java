package com.example.demo.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageBuilder {
    private String book;
    private String message;
    private String time;
}
