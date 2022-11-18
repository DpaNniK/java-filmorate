package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @AfterEach
    public void newDb() {
        jdbcTemplate.update("runscript from 'src/main/resources/schema.sql'");
        jdbcTemplate.update("runscript from 'src/main/resources/data.sql'");
    }

    @Test
    public void testCreateUser() {
        User userTest = createUser(1, "fds@mail.ru");
        userTest.setName("User");
        userStorage.createUser(userTest);

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUsersById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", userTest.getId())
                                .hasFieldOrPropertyWithValue("name", userTest.getName())
                                .hasFieldOrPropertyWithValue("email", userTest.getEmail())
                                .hasFieldOrPropertyWithValue("login", userTest.getLogin())
                                .hasFieldOrPropertyWithValue("birthday", userTest.getBirthday())
                );
    }

    @Test
    public void testUpdateUser() {
        User userTest = createUser(1, "fds@mail.ru");
        userTest.setName("TestUser");
        User userUpdate = createUser(1, "new@yand.ru");
        userUpdate.setName("newTestUser");

        userStorage.createUser(userTest);
        userStorage.updateUser(userUpdate);

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUsersById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", userUpdate.getId())
                                .hasFieldOrPropertyWithValue("name", userUpdate.getName())
                                .hasFieldOrPropertyWithValue("email", userUpdate.getEmail())
                                .hasFieldOrPropertyWithValue("login", userUpdate.getLogin())
                                .hasFieldOrPropertyWithValue("birthday", userUpdate.getBirthday())
                );
    }

    @Test
    public void testGetAllUsers() {
        User user1 = createUser(1, "ya@mail.ru");
        User user2 = createUser(2, "ma@mail.ru");

        userStorage.createUser(user1);
        userStorage.createUser(user2);

        assertThat(userStorage.getUsers()).hasSize(2);
    }

    @Test
    public void testAddFriend() {
        User user1 = createUser(1, "ya@mail.ru");
        User user2 = createUser(2, "ma@mail.ru");

        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userStorage.addFriend(1, 2);

        Optional<Collection<User>> friendList = Optional.ofNullable(userStorage.getFriendList(1));

        assertThat(friendList)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasSize(1)
                );
    }

    @Test
    public void testDeleteFriend() {
        User user1 = createUser(1, "ya@mail.ru");
        User user2 = createUser(2, "ma@mail.ru");

        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userStorage.addFriend(1, 2);
        userStorage.deleteFriend(1, 2);

        Optional<Collection<User>> friendList = Optional.ofNullable(userStorage.getFriendList(1));

        assertThat(friendList)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasSize(0)
                );
    }

    @Test
    public void testCreateFilm() {
        Film filmTest = createFilm(1);

        filmDbStorage.createFilm(filmTest);

        Optional<Film> userOptional = Optional.ofNullable(filmDbStorage.getFilmById(filmTest.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", filmTest.getId())
                                .hasFieldOrPropertyWithValue("rate", filmTest.getRate())
                                .hasFieldOrPropertyWithValue("name", filmTest.getName())
                                .hasFieldOrPropertyWithValue("duration", filmTest.getDuration())
                                .hasFieldOrPropertyWithValue("description", filmTest.getDescription())
                );
    }

    @Test
    public void testUpdateFilm() {
        Film filmTest = createFilm(1);
        Film filmNew = createFilm(1);
        filmNew.setMpa(new Pair(2, "mpa"));

        filmDbStorage.createFilm(filmTest);
        filmDbStorage.updateFilm(filmNew);

        Optional<Film> userOptional = Optional.ofNullable(filmDbStorage.getFilmById(filmTest.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", filmNew.getId())
                                .hasFieldOrPropertyWithValue("rate", filmNew.getRate())
                                .hasFieldOrPropertyWithValue("name", filmNew.getName())
                                .hasFieldOrPropertyWithValue("duration", filmNew.getDuration())
                                .hasFieldOrPropertyWithValue("description", filmNew.getDescription())
                );
    }

    @Test
    public void testGetAllFilms() {
        Film filmTest = createFilm(1);
        Film filmNew = createFilm(2);

        filmDbStorage.createFilm(filmTest);
        filmDbStorage.createFilm(filmNew);

        assertThat(filmDbStorage.getFilms()).hasSize(2);
    }

    @Test
    public void testAddLikeFilm() {
        User user = createUser(1, "paf@ya.ru");
        Film film = createFilm(1);

        userStorage.createUser(user);
        filmDbStorage.createFilm(film);
        filmDbStorage.addLike(film.getId(), user.getId());

        Optional<Set<Integer>> likeList = Optional.ofNullable(filmDbStorage.getFilmById(film.getId()).getLikeList());

        assertThat(likeList)
                .isPresent()
                .hasValueSatisfying(id ->
                        assertThat(id).hasSize(1)
                );
    }

    @Test
    public void testDeleteLike() {
        User user = createUser(1, "paf@ya.ru");
        Film film = createFilm(1);

        userStorage.createUser(user);
        filmDbStorage.createFilm(film);
        filmDbStorage.addLike(film.getId(), user.getId());
        filmDbStorage.deleteLike(film.getId(), user.getId());

        Optional<Set<Integer>> likeList = Optional.ofNullable(filmDbStorage.getFilmById(film.getId()).getLikeList());

        assertThat(likeList)
                .isPresent()
                .hasValueSatisfying(id ->
                        assertThat(id).hasSize(0)
                );
    }

    @Test
    public void testGetPopularFilms() {
        User user = createUser(1, "paf@ya.ru");
        User user2 = createUser(2, "fff@ya.ru");
        Film film = createFilm(1);
        Film film2 = createFilm(2);

        userStorage.createUser(user);
        userStorage.createUser(user2);
        filmDbStorage.createFilm(film);
        filmDbStorage.createFilm(film2);
        filmDbStorage.addLike(film.getId(), user.getId());
        filmDbStorage.addLike(film2.getId(), user.getId());
        filmDbStorage.addLike(film2.getId(), user2.getId());

        Optional<Collection<Film>> popularFilmList = Optional.ofNullable(filmDbStorage.getPopularFilms(2));
        assertThat(popularFilmList)
                .isPresent()
                .hasValueSatisfying(id ->
                        assertThat(id).hasSize(2)
                );
    }

    @Test
    public void testGetAllMap() {
        Optional<Collection<Pair>> mapList = Optional.ofNullable(mpaDbStorage.getAllMpa());
        assertThat(mapList)
                .isPresent()
                .hasValueSatisfying(id ->
                        assertThat(id).hasSize(5)
                );
    }

    @Test
    public void testGetAllGenres() {
        Optional<Collection<Pair>> mapList = Optional.ofNullable(genreDbStorage.getAllGenres());
        assertThat(mapList)
                .isPresent()
                .hasValueSatisfying(id ->
                        assertThat(id).hasSize(6)
                );
    }

    @Test
    public void testGetMapById() {
        Optional<Pair> map = Optional.ofNullable(mpaDbStorage.getMpaById(1));
        assertThat(map)
                .isPresent()
                .hasValueSatisfying(pair ->
                        assertThat(pair).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "G")
                );
    }

    @Test
    public void testGetGenreById() {
        Optional<Pair> map = Optional.ofNullable(genreDbStorage.getGenreById(5));
        assertThat(map)
                .isPresent()
                .hasValueSatisfying(pair ->
                        assertThat(pair).hasFieldOrPropertyWithValue("id", 5)
                                .hasFieldOrPropertyWithValue("name", "Документальный")
                );
    }

    private User createUser(Integer id, String email) {
        User user = new User(email, "log", LocalDate.parse("2022-10-10"));
        user.setId(id);

        return user;
    }

    private Film createFilm(Integer id) {
        Film film = new Film("Film", "desc", LocalDate.parse("2000-10-10"), 120, 5);
        film.setId(id);
        film.setMpa(new Pair(1, "mpa"));
        return film;
    }
}

