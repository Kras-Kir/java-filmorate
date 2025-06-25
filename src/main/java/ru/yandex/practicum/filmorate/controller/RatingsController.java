package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Ratings;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class RatingsController {
    private final FilmService filmService;

    @Autowired
    public RatingsController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Ratings> getAllRatings() {
        return filmService.getAllRatings();
    }

    @GetMapping("/{id}")
    public Ratings getRatingById(@PathVariable int id) {
        return filmService.getRatingById(id);
    }
}

