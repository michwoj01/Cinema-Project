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

    public Flux<Movie> getMoviesWithFilterDTO(MovieFiltersDTO movieFiltersDTO, int page, int maxItemsPerPage) {
        return movieDao.findAllWithFilters(movieFiltersDTO, page, maxItemsPerPage);
    }

    public Mono<Integer> getMovieCountWithFilter(MovieFiltersDTO movieFiltersDTO) {
        return movieDao.getCountWithFilters(movieFiltersDTO);
    }

    public Mono<Movie> getMovieById(long movieId) {
        return movieDao.findById(movieId);
    }

    public Mono<Movie> getMovieByName(String name) {
        return movieDao.getMovieByName(name);
    }

    public Flux<Movie> findAll() {
        return movieDao.findAll();
    }

    public Mono<Void> deleteMovie(long id) {
        return movieDao.deleteById(id);
    }

    public Flux<String> getMostPopularMovies() {
        return movieDao.getMostPopularMovies();
    }

    public Flux<String> getMostDisplayedMovies() {
        return movieDao.getMostTimeDisplayedMovies();
    }
}
