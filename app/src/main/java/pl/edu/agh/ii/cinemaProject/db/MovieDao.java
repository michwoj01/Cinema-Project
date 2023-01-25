package pl.edu.agh.ii.cinemaProject.db;

import io.vavr.Function6;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.edu.agh.ii.cinemaProject.db.dto.MovieFiltersDTO;
import pl.edu.agh.ii.cinemaProject.model.Movie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieDao extends ReactiveCrudRepository<Movie, Long> {
    @Query("""
            SELECT
                 *
             FROM
                 MOVIE
             WHERE
                 (duration between :minDuration and :maxDuration)
             and name like :nameContains
             and case when :isRecommended then MOVIE.id in (select mr.movie_id from RECOMMENDATION mr) else 1=1 end
             limit :maxItemsPerPage offset :startIndex
             """)
    Flux<Movie> findAllWithFilters(int minDuration, int maxDuration, String nameContains, Boolean isRecommended, int startIndex, int maxItemsPerPage);

    default Flux<Movie> findAllWithFilters(MovieFiltersDTO movieFiltersDTO, int page, int maxItemsPerPage) {
        return findAllWithFiltersFromDTO(this::findAllWithFilters, movieFiltersDTO, page, maxItemsPerPage);
    }

    private <R> R findAllWithFiltersFromDTO(Function6<Integer, Integer, String, Boolean, Integer, Integer, R> f, MovieFiltersDTO movieFiltersDTO, int page, int maxItemsPerPage) {
        return f.apply(movieFiltersDTO.minDuration().orElse(0),
                movieFiltersDTO.maxDuration().orElse(60000000),
                "%" + movieFiltersDTO.nameContains().orElse("") + "%",
                movieFiltersDTO.isRecommended().orElse(false),
                maxItemsPerPage * page,
                maxItemsPerPage);
    }

    @Query("""
            SELECT
                count(*)
            FROM
                MOVIE
            WHERE
                (duration between :minDuration and :maxDuration)
            and name like :nameContains
            and case when :isRecommended then MOVIE.id in (select mr.movie_id from RECOMMENDATION mr) else :startIndex=:maxItemsPerPage end
            """)
    Mono<Integer> getCountWithFilters(int minDuration, int maxDuration, String nameContains, Boolean isRecommended, int startIndex, int maxItemsPerPage);

    default Mono<Integer> getCountWithFilters(MovieFiltersDTO movieFiltersDTO) {
        return findAllWithFiltersFromDTO(this::getCountWithFilters, movieFiltersDTO, 0, 0);
    }

    @Query("""
            with sub as (SELECT m.name as name, m.id from movie m
            inner join schedule s on m.id = s.movie_id
            group by m.id
            order by Sum(s.nr_of_seats) desc
            limit 5)
            select name from sub
            """)
    Flux<String> getMostPopularMovies();

    @Query("""
            with sub as (SELECT m.name as name, m.id from movie m
            inner join schedule s on m.id = s.movie_id
            group by m.id
            order by count(s.id) desc
            limit 5)
            select name from sub
            """)
    Flux<String> getMostTimeDisplayedMovies();

    Mono<Movie> getMovieByName(String name);
}
