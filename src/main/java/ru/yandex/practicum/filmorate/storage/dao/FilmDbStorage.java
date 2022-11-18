package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.FilmMapper;

import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) {
        jdbcTemplate.update("INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA)" +
                        " VALUES (?, ?, ?, ?, ?, ?) ", film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId());
        if (film.getGenres() != null) {
            for (Pair pair : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)"
                        , film.getId(), pair.getId());
            }
        }

        return getFilmById(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update("UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, " +
                        "DURATION = ?, RATE = ?, MPA = ? WHERE FILM_ID = ?", film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId(), film.getId());
        jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID =?", film.getId());

        if (film.getGenres() != null) {
            for (Pair pair : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)"
                        , film.getId(), pair.getId());
            }
        }

        return getFilmById(film.getId());
    }

    @Override
    public Collection<Film> getFilms() {
        return jdbcTemplate.query("SELECT * FROM PUBLIC.FILMS LEFT JOIN FILM_GENRES FG " +
                "on FILMS.FILM_ID = FG.FILM_ID", new FilmMapper(jdbcTemplate));
    }

    @Override
    public Film getFilmById(Integer filmId) {
        return jdbcTemplate.queryForObject("SELECT * FROM FILMS WHERE FILM_ID = ?"
                , new FilmMapper(jdbcTemplate), filmId);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        jdbcTemplate.update("INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES ( ?, ? )", filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        jdbcTemplate.update("DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?", filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        Comparator<Film> comparator = Comparator.comparing(Film::getSizeLikeList).reversed()
                .thenComparing(Film::getId);
        Set<Film> sortedPopularFilmList = new TreeSet<>(comparator);
        for (Film film : getFilms()) {
            sortedPopularFilmList.add(getFilmById(film.getId()));
        }

        return sortedPopularFilmList.stream().limit(count).collect(Collectors.toSet());
    }
}
