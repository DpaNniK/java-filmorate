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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FilmorateUserTests {
    @Autowired
    private MockMvc mockMvc;
    Gson gson = NewGson.createGson();

    //Тестирование работы GET-зпроса
    @Test
    public void testEmptyGetMethod() throws Exception {
        this.mockMvc.perform(get("/users")).andDo(print()).andExpect(status().isOk());
    }

    //Тестирование добавления пользователя без присвоения id в запросе
    @Test
    public void testPostMethodForUserWithEmptyId() throws Exception {
        User user = new User("mail@mail.ru", "dolore", LocalDate.parse("1946-08-20"));
        user.setName("Nick Name");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(jsonPath("$.login").value("dolore"))
                .andExpect(jsonPath("$.name").value("Nick Name"))
                .andExpect(jsonPath("$.birthday").value("1946-08-20"));
    }

    //Тестирование добавления пользователя с не указанным в запросе именем, а также id
    @Test
    public void testPostMethodForUserWithEmptyIdAndUserName() throws Exception {
        User user = new User("mail@mail.ru", "common", LocalDate.parse("1946-08-20"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(jsonPath("$.login").value("common"))
                .andExpect(jsonPath("$.name").value("common"))
                .andExpect(jsonPath("$.birthday").value("1946-08-20"));
    }

    //Тестирование PUT для обновления информации о пользователе
    @Test
    public void testPutMethodForUser() throws Exception {
        User user = new User("mail@mail.ru", "dolore", LocalDate.parse("1946-08-20"));
        user.setName("Nick Name");
        User newUser = new User("mail@yandex.ru", "doloreUpdate", LocalDate.parse("1976-09-20"));
        newUser.setName("New User");
        newUser.setId(1);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(user)));
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(newUser)))
                .andExpect(status().isOk());
    }

    //Тестирование PUT с неуказанным в запросе ником
    @Test
    public void testPutMethodForUserWithEmptyName() throws Exception {
        User user = new User("mail@mail.ru", "dolore", LocalDate.parse("1946-08-20"));
        user.setName("Nick Name");
        User newUser = new User("mail@yandex.ru", "doloreUpdate", LocalDate.parse("1976-09-20"));
        newUser.setId(1);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(user)));
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("mail@yandex.ru"))
                .andExpect(jsonPath("$.login").value("doloreUpdate"))
                .andExpect(jsonPath("$.name").value("doloreUpdate"))
                .andExpect(jsonPath("$.birthday").value("1976-09-20"));
    }

    //Тестирование добавления пользователя с неуказанными в запросе данными (получение ошибки 400)
    @Test
    public void testPostMethodForEmptyUser() throws Exception {
        User user = new User(null, null, null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(user)))
                .andExpect(status().is4xxClientError());
    }

    //Тестирование обновления несуществующего пользователя
    @Test
    public void testPutMethodForUnknownUser() throws Exception {
        User newUser = new User("mail@yandex.ru", "doloreUpdate", LocalDate.parse("1976-09-20"));
        newUser.setName("New User");
        newUser.setId(9999);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(newUser)))
                .andExpect(status().is4xxClientError());
    }

}
