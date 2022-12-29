package pl.edu.agh.ii.cinemaProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.MovieDao;
import pl.edu.agh.ii.cinemaProject.db.dto.MovieFiltersDTO;
import pl.edu.agh.ii.cinemaProject.model.Movie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieService {
    @Autowired
    private MovieDao movieDao;

    public Flux<Movie> getMoviesWithFilterDTO(MovieFiltersDTO movieFiltersDTO) {
        if (movieFiltersDTO.isFiltering()) {
            return movieDao.findAllWithFilters(movieFiltersDTO);
        } else {
            return movieDao.findAll();
        }
    }

    public Mono<Movie> getMovieInfo(long movieId) {
        return movieDao.findById(movieId);
    }

    public Flux<Movie> findAll() {
        return movieDao.findAll();
    }

    public Mono<Void> deleteMovie(long id) {
        return movieDao.deleteById(id);
    }
}
