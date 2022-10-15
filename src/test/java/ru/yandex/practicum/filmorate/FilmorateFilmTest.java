package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.gson.NewGson;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureMockMvc
public class FilmorateFilmTest {
    @Autowired
    private MockMvc mockMvc;
    Gson gson = NewGson.createGson();

    //Тестирование работы GET-зпроса
    @Test
    public void testEmptyGetMethod() throws Exception {
        this.mockMvc.perform(get("/films")).andDo(print()).andExpect(status().isOk());
    }

    //Тестирование добавления фильма без присвоения id в запросе
    @Test
    public void testPostMethodForFilmWithEmptyId() throws Exception {
        Film film = new Film("film name", "des", LocalDate.parse("1967-03-25"), 100);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("film name"))
                .andExpect(jsonPath("$.description").value("des"))
                .andExpect(jsonPath("$.releaseDate").value("1967-03-25"))
                .andExpect(jsonPath("$.duration").value(100))
                .andReturn();
    }

    //Тестирование PUT для обновления инф-ции о фильме
    @Test
    public void testPutMethodForFilmWithEmptyId() throws Exception {
        Film film = new Film("film name", "des", LocalDate.parse("1967-03-25"), 100);
        Film newFilm = new Film("New Film", "new desc", LocalDate.parse("1989-04-17"), 190);
        newFilm.setId(1);
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(film)));
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(newFilm)))
                .andExpect(jsonPath("$.name").value("New Film"))
                .andExpect(jsonPath("$.description").value("new desc"))
                .andExpect(jsonPath("$.releaseDate").value("1989-04-17"))
                .andExpect(jsonPath("$.duration").value(190))
                .andReturn();
    }

    //Получение ошибки при неверной дате релиза
    @Test
    public void testPostMethodForFilmWithWrongDateRelease() throws Exception {
        Film film = new Film("film name", "des", LocalDate.parse("1567-03-25"), 100);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(film)))
                .andExpect(status().is4xxClientError());
    }

    //Получение ошибки при обновлении фильма с неправильной датой нового
    @Test
    public void testPutMethodForFilmWithWrongDateRelease() throws Exception {
        Film film = new Film("film name", "des", LocalDate.parse("1967-03-25"), 100);
        Film newFilm = new Film("New Film", "new desc", LocalDate.parse("1389-04-17"), 190);
        newFilm.setId(1);
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(film)));
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(newFilm)))
                .andExpect(status().is4xxClientError());
    }

    //Попытка обновить фильм с несущ. id
    @Test
    public void testPutMethodForUnknownFilm() throws Exception {
        Film film = new Film("film name", "des", LocalDate.parse("1967-03-25"), 100);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(film)))
                .andExpect(status().is4xxClientError());
    }
}
