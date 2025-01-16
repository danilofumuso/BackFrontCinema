package it.epicode.marsiflexbe.movies;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class MovieController {
    private final MovieService movieService;

    @GetMapping("/movies-popular")
    public ResponseEntity< List<Movie>> findAll() {
        return  ResponseEntity.ok(movieService.findAll());
    }

}
