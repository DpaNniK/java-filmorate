package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.storage.dao.mapper.GenreMapper;

import java.util.Collection;

@Component("genreDbStorage")
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Pair getGenreById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT * FROM GENRES WHERE GENRE_ID = ?"
                , new GenreMapper(), id);
    }

    public Collection<Pair> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM GENRES", new GenreMapper());
    }
}
