package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Pair;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("filmMemoryStorage")
public class InMemoryFilmStorage implements FilmStorage {
    public HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Film createFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(Integer filmId) {
        return films.get(filmId);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        films.get(filmId).getLikeList().add(userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        films.get(filmId).getLikeList().remove(userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        Comparator<Film> comparator = Comparator.comparing(Film::getSizeLikeList).reversed()
                .thenComparing(Film::getId);
        Set<Film> sortedPopularFilmList = new TreeSet<>(comparator);

        for (Integer filmId : films.keySet()) {
            sortedPopularFilmList.add(films.get(filmId));
        }
        return sortedPopularFilmList.stream().limit(count).collect(Collectors.toSet());
    }

    @Override
    public Pair getMapById(Integer id) {
        return null;
    }

    @Override
    public Collection<Pair> getAllMap() {
        return null;
    }

    @Override
    public Pair getGenreById(Integer id) {
        return null;
    }

    @Override
    public Collection<Pair> getAllGenres() {
        return null;
    }
}
