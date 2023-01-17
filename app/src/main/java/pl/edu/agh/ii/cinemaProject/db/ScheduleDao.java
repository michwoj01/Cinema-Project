package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.edu.agh.ii.cinemaProject.model.Schedule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.time.LocalDateTime;

public interface ScheduleDao extends ReactiveCrudRepository<Schedule, Long> {

    @Query("UPDATE SCHEDULE SET CURRENTLY_AVAILABLE = CURRENTLY_AVAILABLE - :numberOfTickets " +
            "WHERE schedule.id = :scheduleId")
    Mono<Integer> buyTickets(long scheduleId, int numberOfTickets);

    @Query("SELECT schedule.currently_available-:numberOfTickets from schedule " +
            "where schedule.id = :scheduleId")
    Mono<Integer> checkIfAvailable(long scheduleId, int numberOfTickets);

    @Query("SELECT count(*) from schedule s " +
            "inner join movie m on m.id = s.movie_id " +
            "where s.cinema_hall_id = :cinemaHallId " +
            "and s.start_date between :startDate and :startDate + (m.duration * interval '1 minute')" +
            "and s.id != :id")
    Mono<Integer> getAllByCinemaHallId(long cinemaHallId, LocalDateTime startDate, long id);

    @Query("SELECT * from SCHEDULE " +
            "where SCHEDULE.CURRENTLY_AVAILABLE > 0 " +
            "and SCHEDULE.START_DATE > CURRENT_DATE")
    Flux<Schedule> findAllAvailable();

    @Query("SELECT count(*) from seat t " +
            "inner join schedule s on s.id = t.schedule_id " +
            "group by s.start_date " +
            "order by s.start_date")
    Flux<Integer> getTicketsSoldByDays();

    @Query("SELECT count(*) from schedule s " +
            "group by s.start_date " +
            "order by s.start_date")
    Flux<Integer> getMoviesDisplayedByDays();

    Flux<Date> getDaysForTickets();

    @Query("SELECT s.start_date from schedule s " +
            "group by s.start_date " +
            "order by s.start_date")
    Flux<Date> getDaysForMovies();
}
