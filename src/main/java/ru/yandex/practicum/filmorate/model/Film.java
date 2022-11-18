package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Data
public class Film {

    private Integer id;
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    @NotNull
    private final int rate;
    private List<Pair> genres;
    @NotNull
    private Pair mpa;
    private Set<Integer> likeList = new TreeSet<>();

    public Integer getSizeLikeList() {
        return likeList.size();
    }
}
