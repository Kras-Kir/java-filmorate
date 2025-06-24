package ru.yandex.practicum.filmorate.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Ratings;
import ru.yandex.practicum.filmorate.storage.RatingsStorage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class RatingsDbStorage implements RatingsStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Ratings> getAllRatings() {
        String sql = "SELECT * FROM ratings";
        return jdbcTemplate.query(sql, this::mapRowToRating);
    }

    @Override
    public Optional<Ratings> getRatingById(int id) {
        String sql = "SELECT * FROM ratings WHERE rating_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                    new Ratings(
                            rs.getInt("rating_id"),
                            rs.getString("name")
                    ), id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Ratings getFilmRating(long filmId) {
        String sql = "SELECT r.* FROM ratings r JOIN film f ON r.rating_id = f.rating_id WHERE f.id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToRating, filmId);
    }

    private Ratings mapRowToRating(ResultSet rs, int rowNum) throws SQLException {
        return new Ratings(rs.getInt("rating_id"), rs.getString("name"));
    }
}
