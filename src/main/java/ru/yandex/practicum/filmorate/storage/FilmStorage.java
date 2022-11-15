package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Pair;

import java.util.Collection;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getFilms();

    Film getFilmById(Integer filmId);

    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    Collection<Film> getPopularFilms(Integer count);

    Pair getMapById(Integer id);
    Collection<Pair> getAllMap();

    Pair getGenreById(Integer id);

    Collection<Pair> getAllGenres();
}
