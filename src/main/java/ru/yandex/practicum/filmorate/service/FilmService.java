package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.RequestErrorForFilm;
import ru.yandex.practicum.filmorate.exception.RequestErrorForUser;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService implements FilmStorage {
    private int id = 1;
    private static final LocalDate DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    final FilmStorage filmStorage;

    final UserStorage userStorage;
    final MpaDbStorage mpaDbStorage;
    final GenreDbStorage genreDbStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage
            , @Qualifier("dbStorage") UserStorage userStorage
            , @Qualifier("mpaDbStorage") MpaDbStorage mpaDbStorage
            , @Qualifier("genreDbStorage") GenreDbStorage genreDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
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
            if (film.getGenres() != null) {
                film.setGenres(deleteDuplicateGenre(film.getGenres()));
            }
            log.info("Новый фильм добавлен в список");
            return filmStorage.createFilm(film);
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (checkContainsFilmInList(film.getId())) {
            if (film.getReleaseDate().isBefore(DATE)) {
                log.warn("Пользователь ввел некорректную дату выхода фильма");
                throw new ValidationException(HttpStatus.BAD_REQUEST
                        , "Ошибка. Дата фильма не может быть раньше 28.12.1895");
            } else {
                if (film.getGenres() != null) {
                    film.setGenres(deleteDuplicateGenre(film.getGenres()));
                }
                log.info("Фильм с id {} успешно обновлен", film.getId());
                return filmStorage.updateFilm(film);
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
        return filmStorage.getFilms();
    }

    @Override
    public Film getFilmById(Integer filmId) {
        if (checkContainsFilmInList(filmId)) {
            log.info("Получен запрос фильма с id {}", filmId);
            return filmStorage.getFilmById(filmId);
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
        filmStorage.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        checkContainsUserAndFilm(filmId, userId);
        if (!checkContainsLikeForFilm(filmId, userId)) {
            log.warn("Пользователь {} пытался удалить несуществующий лайк фильму {}"
                    , userId, filmId);
            throw new RequestErrorForFilm(HttpStatus.NOT_FOUND
                    , "Ошибка. Пользователь не ставил лайк этому фильму");
        }
        log.info("Пользователь {} удалил лайк к фильму {}", userId, filmId);
        filmStorage.deleteLike(filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    public Pair getMpaById(Integer id) {
        if (id <= 0 || id > 5) {
            log.warn("Пользователь запросил не существующий MPA под id {}", id);
            throw new RequestErrorForFilm(HttpStatus.NOT_FOUND
                    , "Ошибка. MPA под данным ID не существует");
        }
        log.info("Пользователь запросил MPA под ID = {}", id);
        return mpaDbStorage.getMpaById(id);
    }

    public Collection<Pair> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }

    public Pair getGenreById(Integer id) {
        if (id <= 0 || id > 6) {
            log.warn("Пользователь запросил не существующий GENRE под id {}", id);
            throw new RequestErrorForFilm(HttpStatus.NOT_FOUND
                    , "Ошибка. Жанр под данным ID не существует");
        }
        log.info("Пользователь запросил GENRE под ID = {}", id);
        return genreDbStorage.getGenreById(id);
    }

    public Collection<Pair> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    private void checkContainsUserAndFilm(Integer filmId, Integer userId) {
        if (!checkContainsFilmInList(filmId)) {
            log.warn("Пользователь {} попытался поставить лайк несуществующему фильму с id {}"
                    , userId, filmId);
            throw new RequestErrorForFilm(HttpStatus.NOT_FOUND
                    , "Ошибка. Фильма с таким id не существует");
        }
        if (!checkContainsUser(userId)) {
            log.warn("Пользователь с id {} не найден"
                    , userId);
            throw new RequestErrorForUser(HttpStatus.NOT_FOUND
                    , "Ошибка. Пользователя под id = " + userId + " не существует");
        }
    }

    private boolean checkContainsFilmInList(int filmId) {
        for (Film film : filmStorage.getFilms()) {
            if (film.getId() == filmId) return true;
        }
        return false;
    }

    private boolean checkContainsLikeForFilm(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        for (Integer id : film.getLikeList()) {
            if (id == userId) return true;
        }
        return false;
    }

    private boolean checkContainsUser(int userId) {
        for (User user : userStorage.getUsers()) {
            if (user.getId() == userId) return true;
        }
        return false;
    }

    private List<Pair> deleteDuplicateGenre(Collection<Pair> genres) {
        return genres.stream().sorted(Comparator.comparingInt(Pair::getId))
                .distinct().collect(Collectors.toList());
    }
}
