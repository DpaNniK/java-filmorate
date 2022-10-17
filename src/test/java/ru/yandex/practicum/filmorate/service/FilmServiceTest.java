package ru.yandex.practicum.filmorate.service;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FilmorateApplication.class)
public class FilmServiceTest {
    @Autowired
    FilmService filmService;
    @Autowired
    InMemoryFilmStorage inMemoryFilmStorage;
    @Autowired
    InMemoryUserStorage inMemoryUserStorage;

    @AfterEach
    @Autowired
    public void afterEach() {
        this.inMemoryFilmStorage = new InMemoryFilmStorage();
        this.inMemoryUserStorage = new InMemoryUserStorage();
        this.filmService = new FilmService(inMemoryFilmStorage, inMemoryUserStorage);
    }

    @Test
    public void correctCreateFilmTest() {
        Film film = new Film("film name", "des"
                , LocalDate.parse("1967-03-25"), 100);
        filmService.createFilm(film);

        assertNotNull(filmService.inMemoryFilmStorage.films, "Ошибка. Фильм не добавился в список");
        assertEquals(filmService.inMemoryFilmStorage.films.size(), 1
                , "Ошибка. В хранилище неверное количество фильмов");

        Film filmFromStorage = filmService.inMemoryFilmStorage.films.get(1);
        assertEquals(filmFromStorage.getId(), 1, "Неверное присвоен ID для фильма");
        assertEquals(filmFromStorage.getReleaseDate(), film.getReleaseDate()
                , "Неверно присвоена дата релиза фильма");
        assertEquals(filmFromStorage.getName(), film.getName()
                , "Неверно присвоено название фильма");
        assertEquals(filmFromStorage.getDescription(), film.getDescription()
                , "Неверно присвоено описание для фильма");
        assertEquals(filmFromStorage.getDuration(), film.getDuration()
                , "Неверно присвоена продолжительность фильма");
    }

    @Test
    public void correctUpdateFilmTest() {
        Film film = new Film("film name", "des"
                , LocalDate.parse("1967-03-25"), 100);
        Film newFilm = new Film("New Film", "new desc"
                , LocalDate.parse("1989-04-17"), 190);
        newFilm.setId(1);

        filmService.createFilm(film);
        filmService.updateFilm(newFilm);

        Film filmFromStorage = filmService.inMemoryFilmStorage.films.get(1);
        assertEquals(filmFromStorage.getId(), 1, "Неверное присвоен ID для фильма");
        assertEquals(filmFromStorage.getReleaseDate(), newFilm.getReleaseDate()
                , "Неверно присвоена дата релиза фильма");
        assertEquals(filmFromStorage.getName(), newFilm.getName()
                , "Неверно присвоено название фильма");
        assertEquals(filmFromStorage.getDescription(), newFilm.getDescription()
                , "Неверно присвоено описание для фильма");
        assertEquals(filmFromStorage.getDuration(), newFilm.getDuration()
                , "Неверно присвоена продолжительность фильма");
    }

    @Test
    public void correctAddLikeForFilm() {
        Film film = new Film("film name", "des"
                , LocalDate.parse("1967-03-25"), 100);
        film.setId(1);
        User user = new User("mail@mail.ru", "dolore"
                , LocalDate.parse("1946-08-20"));
        user.setId(1);
        inMemoryUserStorage.users.put(user.getId(), user);

        filmService.createFilm(film);
        filmService.addLike(film.getId(), user.getId());

        assertNotNull(filmService.getFilmById(film.getId()).getLikeList()
                , "Ошибка. Фильму не поставился лайк");
        assertEquals(filmService.getFilmById(film.getId()).getSizeLikeList(), 1
                , "Ошибка. Неверное число лайков у фильма");
    }

    @Test
    public void correctDeleteLikeForFilm() {
        Film film = new Film("film name", "des"
                , LocalDate.parse("1967-03-25"), 100);
        film.setId(1);
        User user = new User("mail@mail.ru", "dolore"
                , LocalDate.parse("1946-08-20"));
        user.setId(1);
        inMemoryUserStorage.users.put(user.getId(), user);

        filmService.createFilm(film);
        filmService.addLike(film.getId(), user.getId());
        filmService.deleteLike(film.getId(), user.getId());

        assertEquals(filmService.getFilmById(film.getId()).getSizeLikeList(), 0
                , "Ошибка. Лайк для фильма не удален");
    }

    @Test
    public void get404StatusForGetFalseFilmId() {
        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                generateExecutableForFalseFilmId());
        assertEquals(ex.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void get404StatusForDeleteFalseLikeFilmId() {
        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                generateExecutableForFalseLikeFilmId());
        assertEquals(ex.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void get404StatusForAddLikeFalseFilmId() {
        ResponseStatusException ex = Assertions.assertThrows(
                ResponseStatusException.class,
                generateExecutableForAddLikeFalseFilmId());
        assertEquals(ex.getStatus(), HttpStatus.NOT_FOUND);
    }

    private Executable generateExecutableForFalseFilmId() {
        return () -> filmService.getFilmById(1);
    }

    private Executable generateExecutableForFalseLikeFilmId() {
        return () -> filmService.deleteLike(1, 1);
    }

    private Executable generateExecutableForAddLikeFalseFilmId() {
        return () -> filmService.addLike(1, 1);
    }
}
