package ru.yandex.practicum.filmorate.storage.dao.mapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

//Основной маппер для FilmDbStorage
//Здесь создается объект класса Film со всеми параметрами, в том числе со списком лайков, жанров, а также rate и mpa
public class FilmMapper implements RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;

    public FilmMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(rs.getString("NAME"), rs.getString("DESCRIPTION"),
                rs.getDate("RELEASE_DATE").toLocalDate(), rs.getInt("DURATION")
                , rs.getInt("RATE"));
        film.setId(rs.getInt("FILM_ID"));

        Pair pairMpa = jdbcTemplate.queryForObject("SELECT * FROM MPA WHERE MPA_ID = ?", new PairMapper()
                , rs.getInt("MPA"));
        film.setMpa(pairMpa);

        List<Pair> pairGenre = jdbcTemplate.query("SELECT FILM_GENRES.GENRE_ID, " +
                        "G2.GENRE_NAME FROM FILM_GENRES LEFT JOIN GENRES G2 on G2.GENRE_ID = FILM_GENRES.GENRE_ID\n" +
                        "WHERE FILM_GENRES.FILM_ID=?;"
                , new GenreMapper(), rs.getInt("FILM_ID"));
        film.setGenres(pairGenre);

        List<Integer> filmLikeList = jdbcTemplate.query("SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID = ?",
                new LikeMapper(), film.getId());

        film.setLikeList(new HashSet<>(filmLikeList));

        return film;
    }
}
