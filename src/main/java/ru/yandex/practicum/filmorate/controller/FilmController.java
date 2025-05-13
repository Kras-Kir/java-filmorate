package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 1;

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Получен запрос POST /films - {}", film);
        validateFilm(film);
        film.setId(idCounter++);
        films.put(film.getId(), film);
        log.info("Создан новый фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Получен запрос PUT /films - {}", film);
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            String errorMsg = "Фильм с ID " + film.getId() + " не найден";
            log.error(errorMsg);
            throw new ValidationException(errorMsg);
        }
        films.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            String errorMsg = "Название фильма не может быть пустым";
            log.warn(errorMsg + " - {}", film);
            throw new ValidationException(errorMsg);
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String errorMsg = "Описание фильма не может превышать 200 символов";
            log.warn(errorMsg + " - {}", film);
            throw new ValidationException(errorMsg);
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            String errorMsg = "Дата релиза не может быть раньше 28 декабря 1895 года";
            log.warn(errorMsg + " - {}", film);
            throw new ValidationException(errorMsg);
        }
        if (film.getDuration() <= 0) {
            String errorMsg = "Продолжительность фильма должна быть положительной";
            log.warn(errorMsg + " - {}", film);
            throw new ValidationException(errorMsg);
        }
    }
}
