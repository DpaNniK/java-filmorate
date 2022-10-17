package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RequestErrorForUser extends ResponseStatusException {
    public RequestErrorForUser(HttpStatus status, String message) {
        super(status, message);
    }
}

