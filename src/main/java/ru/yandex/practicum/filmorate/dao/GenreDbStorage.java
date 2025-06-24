package ru.yandex.practicum.filmorate.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                    new Genre(rs.getInt("genre_id"), rs.getString("name")), id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getFilmGenres(long filmId) {
        String sql = "SELECT g.* FROM genre g JOIN genres_film gf ON g.genre_id = gf.genre_id WHERE gf.film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }

    @Override
    public void addGenreToFilm(long filmId, int genreId) {
        String sql = "INSERT INTO genres_film (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public void removeGenresFromFilm(long filmId) {
        String sql = "DELETE FROM genres_film WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }
}
