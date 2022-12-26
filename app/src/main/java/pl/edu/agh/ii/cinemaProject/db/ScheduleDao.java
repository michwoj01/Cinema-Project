package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.edu.agh.ii.cinemaProject.model.Schedule;
import reactor.core.publisher.Mono;

public interface ScheduleDao extends ReactiveCrudRepository<Schedule, Long> {

    @Query("UPDATE SCHEDULE SET CURRENTLY_AVAILABLE = CURRENTLY_AVAILABLE - :numberOfTickets WHERE schedule.id = :scheduleId")
    Mono<Integer> buyTickets(long scheduleId, int numberOfTickets);

    @Query("SELECT schedule.currently_available-:numberOfTickets from schedule where schedule.id = :scheduleId")
    Mono<Integer> checkIfAvailable(long scheduleId, int numberOfTickets);
}
