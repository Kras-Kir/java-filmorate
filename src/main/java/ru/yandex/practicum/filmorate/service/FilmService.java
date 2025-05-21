package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        checkUserExists(userId);

        if (!film.getLikes().add(userId)) {
            throw new IllegalArgumentException("Пользователь уже поставил лайк этому фильму");
        }
    }

    public void removeLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        checkUserExists(userId);

        if (!film.getLikes().remove(userId)) {
            throw new NotFoundException("Лайк от пользователя с ID " + userId + " не найден");
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmOrThrow(long filmId) {
        return filmStorage.getAllFilms().stream()
                .filter(f -> f.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
    }

    private void checkUserExists(long userId) {
        userStorage.getAllUsers().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }
}
