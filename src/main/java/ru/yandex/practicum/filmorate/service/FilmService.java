package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Ratings;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.RatingsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final RatingsStorage ratingsStorage;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            GenreStorage genreStorage,
            RatingsStorage ratingsStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.ratingsStorage = ratingsStorage;
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с ID " + id + " не найден"));
    }

    public List<Ratings> getAllRatings() {
        return ratingsStorage.getAllRatings();
    }

    public Ratings getRatingById(int id) {
        return ratingsStorage.getRatingById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с ID " + id + " не найден"));
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        validateMpaExists(film.getMpa());
        validateGenresExist(film.getGenres());

        Film createdFilm = filmStorage.createFilm(film);
        updateFilmGenres(createdFilm.getId(), film.getGenres());
        return getFilmById(createdFilm.getId());
    }

    public Film updateFilm(Film film) {
        getFilmOrThrow(film.getId()); // Проверяем существование фильма
        validateMpaExists(film.getMpa());
        validateGenresExist(film.getGenres());

        filmStorage.updateFilm(film);
        updateFilmGenres(film.getId(), film.getGenres());
        return getFilmById(film.getId());
    }

    private void updateFilmGenres(long filmId, List<Genre> genres) {
        genreStorage.removeGenresFromFilm(filmId);
        if (genres != null && !genres.isEmpty()) {
            genres.stream()
                    .map(Genre::getId)
                    .distinct()
                    .forEach(genreId -> {
                        genreStorage.addGenreToFilm(filmId, genreId);
                    });
        }
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может превышать 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private void validateMpaExists(Ratings mpa) {
        if (mpa != null) {
            ratingsStorage.getRatingById(mpa.getId())
                    .orElseThrow(() -> new NotFoundException("MPA рейтинг с ID " + mpa.getId() + " не найден"));
        }
    }

    private void validateGenresExist(List<Genre> genres) {
        if (genres != null) {
            for (Genre genre : genres) {
                // Не проверяем genre.getId() == null, так как int не может быть null
                genreStorage.getGenreById(genre.getId())
                        .orElseThrow(() -> new NotFoundException("Жанр с ID " + genre.getId() + " не найден"));
            }
        }
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return getFilmOrThrow(id);
    }

    public void addLike(long filmId, long userId) {
        getFilmOrThrow(filmId);
        checkUserExists(userId);

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        getFilmOrThrow(filmId);
        checkUserExists(userId);

        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private Film getFilmOrThrow(long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
    }

    private void checkUserExists(long userId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }
}
