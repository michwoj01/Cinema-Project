package pl.edu.agh.ii.cinemaProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.CinemaHallInMemoryDao;
import pl.edu.agh.ii.cinemaProject.model.CinemaHall;
import reactor.core.publisher.Mono;

@Service
public class CinemaHallService {
    @Autowired
    private CinemaHallInMemoryDao cinemaHallDao;

    public Mono<CinemaHall> getCinemaHallByScheduleId(long id) {
        return cinemaHallDao.getCinemaHallById(id);
    }
}
