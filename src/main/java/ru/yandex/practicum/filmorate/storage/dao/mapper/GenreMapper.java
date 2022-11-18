package ru.yandex.practicum.filmorate.storage.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
//Маппер для пар Genres фильма
public class GenreMapper implements RowMapper<Pair> {

    @Override
    public Pair mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Pair(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME"));
    }
}
