package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.edu.agh.ii.cinemaProject.model.Seat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SeatDao extends ReactiveCrudRepository<Seat, Long> {

    @Query("SELECT * FROM seat " +
            "inner join schedule s on s.id = seat.schedule_id " +
            "WHERE s.id = :scheduleId")
    Flux<Seat> getTakenSeats(long scheduleId);
    @Query("SELECT COUNT(*) FROM seat " +
            "inner join schedule s on s.id = seat.schedule_id " +
            "WHERE s.id = :scheduleId")
    Mono<Integer> countTakenSeats(long scheduleId);
    @Query("DELETE FROM seat WHERE schedule_id = :scheduleId")
    Mono<Void> deleteByScheduleId(long scheduleId);
}
