package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Data
public class User {

    private Integer id;
    @Email
    private final String email;
    @NotBlank
    @Pattern(regexp = "[^ ]{1,}$")
    private final String login;
    private String name;
    @PastOrPresent
    private final LocalDate birthday;
    private Set<Integer> friendList = new TreeSet<>();
}
