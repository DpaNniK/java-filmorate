package ru.yandex.practicum.filmorate.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NewGson {
    //Класс, в котором описана логика сериализации и десериализации полей LocalDateTime в объектах
    static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {

                    @Override
                    public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
                        jsonWriter.value(localDate.format(DATE_TIME_FORMATTER));
                    }

                    @Override
                    public LocalDate read(JsonReader jsonReader) throws IOException {
                        return LocalDate.parse(jsonReader.nextString(), DATE_TIME_FORMATTER);
                    }
                }
                        .nullSafe())
                .registerTypeAdapter(Duration.class, new TypeAdapter<Duration>() {

                    @Override
                    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
                        if (duration != null) {
                            jsonWriter.value(duration.toMinutes());
                        }
                    }

                    @Override
                    public Duration read(JsonReader jsonReader) throws IOException {
                        return Duration.ofMinutes(jsonReader.nextLong());
                    }
                }
                        .nullSafe())
                .create();
    }
}

