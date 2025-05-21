package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film createFilm(Film film);
    Film updateFilm(Film film);
    Collection<Film> getAllFilms();
    void deleteFilm(long id);
    Film getFilmById(long id);
}
