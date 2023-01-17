package pl.edu.agh.ii.cinemaProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.TicketDao;
import pl.edu.agh.ii.cinemaProject.model.Ticket;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TicketService {

    @Autowired
    TicketDao ticketDao;

    public Flux<Ticket> getTakenSeats(long scheduleId){return ticketDao.getTakenSeats(scheduleId);}
    public Mono<Ticket> takeSeat(Ticket ticket) {
        return ticketDao.save(ticket);
    }
}
