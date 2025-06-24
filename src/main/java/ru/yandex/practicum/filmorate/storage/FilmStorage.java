package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAllFilms();
    Optional<Film> getFilmById(long id);
    Film createFilm(Film film);
    Film updateFilm(Film film);
    void deleteFilm(long id);
    void addLike(long filmId, long userId);
    void removeLike(long filmId, long userId);
    List<Film> getPopularFilms(int count);
}
