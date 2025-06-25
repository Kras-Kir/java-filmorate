package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Получен запрос POST /films - {}", film);
        validateFilm(film);
        Film createdFilm = filmService.createFilm(film);
        log.info("Создан новый фильм: {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Получен запрос PUT /films - {}", film);
        validateFilm(film);
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Обновлен фильм: {}", updatedFilm);
        return updatedFilm;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен запрос GET /films");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        log.info("Получен запрос GET /films/{}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен запрос PUT /films/{}/like/{}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен запрос DELETE /films/{}/like/{}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос GET /films/popular?count={}", count);
        return filmService.getPopularFilms(count);
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
