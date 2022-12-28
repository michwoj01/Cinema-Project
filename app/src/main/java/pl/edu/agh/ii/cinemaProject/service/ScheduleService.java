package pl.edu.agh.ii.cinemaProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.ScheduleDao;
import pl.edu.agh.ii.cinemaProject.model.Schedule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleDao scheduleDao;

    public Mono<Boolean> buyTickets(long scheduleId, int numberOfTickets) {
        return scheduleDao.checkIfAvailable(scheduleId, numberOfTickets).flatMap((leftAfterSale) -> {
            if (leftAfterSale < 0) {
                return Mono.just(0);
            } else {
                return scheduleDao.buyTickets(scheduleId, numberOfTickets);
            }
        }).map((numOfRowsUpdated) -> numOfRowsUpdated > 0);
    }

    public Mono<Schedule> insertOrUpdateSchedule(Schedule schedule) {
        return scheduleDao.save(schedule);
    }

    public Flux<Schedule> findAll() {
        return scheduleDao.findAll();
    }

    public Flux<Schedule> findAllAvailable() {return scheduleDao.findAllAvailable();}
}
