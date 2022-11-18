package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.storage.dao.mapper.PairMapper;

import java.util.Collection;

@Component("mpaDbStorage")
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Pair getMpaById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT * FROM MPA WHERE MPA_ID = ?", new PairMapper()
                , id);
    }

    public Collection<Pair> getAllMpa() {
        return jdbcTemplate.query("SELECT * FROM MPA", new PairMapper());
    }
}
