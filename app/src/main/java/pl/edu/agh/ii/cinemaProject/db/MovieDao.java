package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.edu.agh.ii.cinemaProject.db.dto.MovieFiltersDTO;
import pl.edu.agh.ii.cinemaProject.model.Movie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieDao extends ReactiveCrudRepository<Movie, Long> {

    @Query("SELECT * from MOVIE WHERE (duration between :minDuration and :maxDuration) and name like :nameContains limit :maxItemsPerPage offset :startIndex")
    Flux<Movie> findAllWithFilters(int minDuration, int maxDuration, String nameContains,int startIndex,int maxItemsPerPage);

    default Flux<Movie> findAllWithFilters(MovieFiltersDTO movieFiltersDTO,int page,int maxItemsPerPage) {
        return findAllWithFilters(
                movieFiltersDTO.minDuration().orElse(0),
                movieFiltersDTO.maxDuration().orElse(60000000),
                "%" + movieFiltersDTO.nameContains().orElse("") + "%",
                maxItemsPerPage*(page),
                maxItemsPerPage
        );
    }

    @Query("SELECT COUNT(*) from MOVIE WHERE (duration between :minDuration and :maxDuration) and name like :nameContains")
    Mono<Integer> getCountWithFilters(int minDuration, int maxDuration, String nameContains);

    default Mono<Integer> getCountWithFilters(MovieFiltersDTO movieFiltersDTO) {
        return getCountWithFilters(
                movieFiltersDTO.minDuration().orElse(0),
                movieFiltersDTO.maxDuration().orElse(60000000),
                "%" + movieFiltersDTO.nameContains().orElse("") + "%"
        );
    }
}
