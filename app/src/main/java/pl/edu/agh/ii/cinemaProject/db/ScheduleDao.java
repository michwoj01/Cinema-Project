package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.edu.agh.ii.cinemaProject.model.Schedule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ScheduleDao extends ReactiveCrudRepository<Schedule, Long> {

    @Query(""" 
            SELECT count(*) from schedule s
            inner join movie m on m.id = s.movie_id
            where s.cinema_hall_id = :cinemaHallId
            and s.start_date between :startDate and :startDate + (m.duration * interval '1 minute')
            and s.id != :id
            """)
    Mono<Integer> getAllByCinemaHallId(long cinemaHallId, LocalDateTime startDate, long id);

    @Query("SELECT * from SCHEDULE where SCHEDULE.START_DATE>CURRENT_DATE")
    Flux<Schedule> findAllAvailable();

    @Query("SELECT count(*) from schedule s group by date(s.start_date) order by date(s.start_date)")
    Flux<Integer> getMoviesDisplayedByDays();

    @Query("SELECT TO_CHAR(date(s.start_date), 'MM-DD')  from schedule s group by date(s.start_date) order by date(s.start_date)")
    Flux<String> getDaysForMovies();

    @Query("SELECT count(*) from ticket t group by date(t.date_of_purchase) order by date(t.date_of_purchase)")
    Flux<Integer> getTicketsSoldByDays();

    @Query("SELECT TO_CHAR(date(t.date_of_purchase), 'MM-DD') from ticket t group by date(t.date_of_purchase) order by date(t.date_of_purchase)")
    Flux<String> getDaysForTickets();

    @Query("Select (SELECT nr_of_seats FROM SCHEDULE WHERE SCHEDULE.id=:scheduleId)-(SELECT COUNT(*) from TICKET where TICKET.Schedule_id=:scheduleId)")
    Mono<Integer> countAvailableSeats(long scheduleId);
}
