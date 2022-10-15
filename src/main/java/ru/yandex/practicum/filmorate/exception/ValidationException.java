package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ValidationException extends ResponseStatusException {

    public ValidationException(HttpStatus status, String message) {
        super(status, message);
    }
}
