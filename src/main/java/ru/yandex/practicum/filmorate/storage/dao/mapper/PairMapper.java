package ru.yandex.practicum.filmorate.storage.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
//Маппер для пары MPA
public class PairMapper implements RowMapper<Pair> {

    @Override
    public Pair mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Pair(rs.getInt("MPA_ID"), rs.getString("MPA_NAME"));
    }
}
