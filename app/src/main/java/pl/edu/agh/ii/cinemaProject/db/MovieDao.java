package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.edu.agh.ii.cinemaProject.db.dto.MovieFiltersDTO;
import pl.edu.agh.ii.cinemaProject.model.Movie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieDao extends ReactiveCrudRepository<Movie, Long> {

    @Query("SELECT * from MOVIE WHERE (duration between :minDuration and :maxDuration) and name like :nameContains limit 50")
    Flux<Movie> findAllWithFilters(int minDuration, int maxDuration, String nameContains);

    default Flux<Movie> findAllWithFilters(MovieFiltersDTO movieFiltersDTO) {
        return findAllWithFilters(
                movieFiltersDTO.minDuration().orElse(0),
                movieFiltersDTO.maxDuration().orElse(60000000),
                "%" + movieFiltersDTO.nameContains().orElse("") + "%"
        );
    }

    @Query("SELECT DISTINCT * from MOVIE mv " +
            "inner join SCHEDULE sch on sch.movie_id = mv.id " +
            "WHERE sch.id = :scheduleId")
    Mono<Movie> getMovieByScheduleId(long scheduleId);

}
