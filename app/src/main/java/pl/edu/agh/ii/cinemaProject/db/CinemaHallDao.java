package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.edu.agh.ii.cinemaProject.model.CinemaHall;
import reactor.core.publisher.Mono;

public interface CinemaHallDao extends ReactiveCrudRepository<CinemaHall, Long> {

    //TODO delete that dao and use config json in service class

    @Query("SELECT DISTINCT * from CINEMA_HALL ch " +
            "inner join SCHEDULE sch on sch.cinema_hall_id = ch.id " +
            "WHERE sch.id = :scheduleId")
    Mono<CinemaHall> getCinemaHallByScheduleId(long scheduleId);
}
