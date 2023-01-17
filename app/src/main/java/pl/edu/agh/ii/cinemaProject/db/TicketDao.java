package pl.edu.agh.ii.cinemaProject.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.edu.agh.ii.cinemaProject.model.Ticket;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TicketDao extends ReactiveCrudRepository<Ticket, Long> {

    @Query("SELECT * FROM ticket " +
            "inner join schedule s on s.id = ticket.schedule_id " +
            "WHERE s.id = :scheduleId")
    Flux<Ticket> getTakenSeats(long scheduleId);

    @Query("""
            SELECT COUNT(*) FROM ticket 
            inner join schedule s on s.id = ticket.schedule_id 
            WHERE s.id = :scheduleId
            """)
    Mono<Integer> countTakenSeats(long scheduleId);
}
