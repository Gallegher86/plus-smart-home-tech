package ru.yandex.practicum.exceptions.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Value
@Builder
public class ErrorResponse {
    HttpStatus status;
    String error;
    String message;
    String userMessage;
    String path;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;
}
