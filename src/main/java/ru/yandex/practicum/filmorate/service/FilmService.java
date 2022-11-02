package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.RequestErrorForFilm;
import ru.yandex.practicum.filmorate.exception.RequestErrorForUser;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

@Service
@Slf4j
public class FilmService implements FilmStorage {
    private int id = 1;
    private static final LocalDate DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    InMemoryFilmStorage inMemoryFilmStorage;
    InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(DATE)) {
            log.warn("Ошибка. Пользователем указана неверная дата выхода фильма");
            throw new ValidationException(HttpStatus.BAD_REQUEST
                    , "Ошибка. Указана неверная дата выхода фильма");
        } else {
            if (film.getId() == null) {
                film.setId(id);
                log.info("Фильму присвоен id = {} автоматически", film.getId());
                id++;
            }
            log.info("Новый фильм добавлен в список");
            return inMemoryFilmStorage.createFilm(film);
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (inMemoryFilmStorage.films.containsKey(film.getId())) {
            if (film.getReleaseDate().isBefore(DATE)) {
                log.warn("Пользователь ввел некорректную дату выхода фильма");
                throw new ValidationException(HttpStatus.BAD_REQUEST
                        , "Ошибка. Дата фильма не может быть раньше 28.12.1895");
            } else {
                log.info("Фильм с id {} успешно обновлен", film.getId());
                return inMemoryFilmStorage.updateFilm(film);
            }
        } else {
            log.warn("Пользователь указал не существующий id для обновления фильма");
            throw new ValidationException(HttpStatus.NOT_FOUND
                    , "Ошибка. Такого фильма не найдено");
        }
    }

    @Override
    public Collection<Film> getFilms() {
        log.info("Получен GET запрос списка фильмов");
        return inMemoryFilmStorage.getFilms();
    }

    @Override
    public Film getFilmById(Integer filmId) {
        if (inMemoryFilmStorage.films.containsKey(filmId)) {
            log.info("Получен запрос фильма с id {}", filmId);
            return inMemoryFilmStorage.getFilmById(filmId);
        } else {
            log.warn("Пользователь запросил несуществующий фильм с id {}", filmId);
            throw new RequestErrorForFilm(HttpStatus.NOT_FOUND
                    , "Ошибка. Запрошенного фильма с id" + filmId + "не существует");
        }
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        checkContainsUserAndFilm(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        inMemoryFilmStorage.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        checkContainsUserAndFilm(filmId, userId);
        if (!inMemoryFilmStorage.films.get(filmId).getLikeList().contains(userId)) {
            log.warn("Пользователь {} пытался удалить несуществующий лайк фильму {}"
                    , userId, filmId);
            throw new RequestErrorForFilm(HttpStatus.NOT_FOUND
                    , "Ошибка. Пользователь не ставил лайк этому фильму");
        }
        log.info("Пользователь {} удалил лайк к фильму {}", userId, filmId);
        inMemoryFilmStorage.deleteLike(filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        return inMemoryFilmStorage.getPopularFilms(count);
    }

    private void checkContainsUserAndFilm(Integer filmId, Integer userId) {
        if (!inMemoryFilmStorage.films.containsKey(filmId)) {
            log.warn("Пользователь {} попытался поставить лайк несуществующему фильму с id {}"
                    , userId, filmId);
            throw new RequestErrorForFilm(HttpStatus.NOT_FOUND
                    , "Ошибка. Фильма с таким id не существует");
        }
        if (!inMemoryUserStorage.users.containsKey(userId)) {
            log.warn("Пользователь с id {} не найден"
                    , userId);
            throw new RequestErrorForUser(HttpStatus.NOT_FOUND
                    , "Ошибка. Пользователя под id = " + userId + " не существует");
        }
    }
}
