package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.edu.agh.ii.cinemaProject.model.Recommendation;
import reactor.core.publisher.Mono;

public interface RecommendationDao extends ReactiveCrudRepository<Recommendation, Long> {

    @Query("DELETE FROM RECOMMENDATION WHERE movie_id = :movieId")
    Mono<Void> deleteByMovieId(long movieId);
}
