package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Ratings;

import java.util.List;
import java.util.Optional;

public interface RatingsStorage {
    List<Ratings> getAllRatings();
    Optional<Ratings> getRatingById(int id);
    Ratings getFilmRating(long filmId);
}
