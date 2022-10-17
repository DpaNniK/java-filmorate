package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RequestErrorForFilm extends ResponseStatusException {
    public RequestErrorForFilm(HttpStatus status, String message) {
        super(status, message);
    }
}
