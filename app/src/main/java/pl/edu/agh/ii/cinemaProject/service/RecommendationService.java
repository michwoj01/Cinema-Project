package pl.edu.agh.ii.cinemaProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.RecommendationDao;
import pl.edu.agh.ii.cinemaProject.model.Recommendation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RecommendationService {

    @Autowired
    private RecommendationDao recommendationDao;


    public Flux<Recommendation> getRecomendedMovies() {
        return recommendationDao.findAll();
    }

    public Mono<Recommendation> addRecommendation(Recommendation recommendation) {
        return recommendationDao.save(recommendation);
    }

    public Mono<Void> deleteRecommendationByMovieId(long movieId) {
        return recommendationDao.deleteByMovieId(movieId);
    }
}
