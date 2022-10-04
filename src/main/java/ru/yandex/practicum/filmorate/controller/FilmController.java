package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")

public class FilmController {

    HashMap<Integer, Film> films = new HashMap<>();
    private int id = 1;
    private static final LocalDate DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    @GetMapping()
    public Collection<Film> getFilms() {
        log.info("Получен GET запрос списка фильмов");
        return films.values();
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(DATE)) {
            log.warn("Ошибка. Пользователем казана неверная дата выхода фильма");
            throw new ValidationException("Ошибка. Указана неверная дата выхода фильма");
        } else {
            if (film.getId() == null) {
                film.setId(id);
                log.info("Фильму присвоен id = {} автоматически", film.getId());
                id++;
            }
            log.info("Новый фильм добавлен в список");
            films.put(film.getId(), film);
        }
        return film;
    }

    @PutMapping()
    public Film putFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            if (film.getReleaseDate().isBefore(DATE)) {
                log.warn("Пользователь ввел некорректную дату выхода фильма");
                throw new ValidationException("Ошибка. Дата фильма не может быть раньше 28.12.1895");
            } else {
                log.info("Фильм с id {} успешно обновлен", film.getId());
                films.put(film.getId(), film);
            }
        } else {
            log.warn("Пользователь указал не существующий id для обновления фильма");
            throw new ValidationException("Ошибка. Такого фильма не найдено");
        }
        return film;
    }

}
