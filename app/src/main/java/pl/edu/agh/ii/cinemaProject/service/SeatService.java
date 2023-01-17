package pl.edu.agh.ii.cinemaProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.SeatDao;
import pl.edu.agh.ii.cinemaProject.model.Seat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SeatService {
    @Autowired
    SeatDao seatDao;

    public Flux<Seat> getTakenSeats(long scheduleId) {
        return seatDao.getTakenSeats(scheduleId);
    }

    public Mono<Seat> takeSeat(Seat seat) {
        return seatDao.save(seat);
    }
}
