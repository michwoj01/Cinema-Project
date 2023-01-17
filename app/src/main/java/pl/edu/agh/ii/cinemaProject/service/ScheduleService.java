package pl.edu.agh.ii.cinemaProject.service;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.ScheduleDao;
import pl.edu.agh.ii.cinemaProject.db.TicketDao;
import pl.edu.agh.ii.cinemaProject.model.Schedule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleDao scheduleDao;
    @Autowired
    private TicketDao ticketDao;

    public Mono<Integer> countAvailableSeats(long scheduleId) {
        return scheduleDao.countAvailableSeats(scheduleId);
    }

    public Either<String, Schedule> createOrUpdateSchedule(Schedule schedule) {
        return validateSchedule(schedule).flatMap((unused) -> Try.ofCallable(() -> scheduleDao.save(schedule).block()).fold((error) -> Either.left("Unknown error"), Either::right));
    }

    public Flux<Schedule> findAllAvailable() {
        return scheduleDao.findAllAvailable();
    }

    public Flux<Schedule> findAll() {
        return scheduleDao.findAll();
    }

    public Either<String, Void> deleteSchedule(Schedule schedule) {
        return checkTickets(schedule).flatMap((unused) -> Try.ofCallable(() -> scheduleDao.deleteById(schedule.getId()).block()).fold((error) -> Either.left("Unknown error"), Either::right));
    }

    private Either<String, ?> checkTickets(Schedule schedule) {
        if (Objects.equals(ticketDao.countTakenSeats(schedule.getId()).block(), 0)) {
            return Either.right(Option.none());
        } else {
            return Either.left("Someone has already purchased a ticket changes are not possible");
        }
    }

    private Either<String, ?> checkStartDate(Schedule schedule) {
        if (schedule.getStart_date().isBefore(LocalDateTime.now())) {
            return Either.left("You cannot add schedule for past");
        } else {
            return Either.right(Option.none());
        }
    }

    private Either<String, ?> checkEmptyReferences(Schedule schedule) {
        if (schedule.getMovie_id() <= 0 || schedule.getCinema_hall_id() <= 0) {
            return Either.left("Movie and hall must be filled");
        } else {
            return Either.right(Option.none());
        }
    }

    private Either<String, ?> checkIfHallIsFree(Schedule schedule) {
        if (!Objects.equals(scheduleDao.getAllByCinemaHallId(schedule.getCinema_hall_id(), schedule.getStart_date(), schedule.getId()).block(), 0)) {
            return Either.left("There is already scheduled movie at this time and hall");
        } else {
            return Either.right(Option.none());
        }
    }

    private Either<String, ?> validateSchedule(Schedule schedule) {
        return checkEmptyReferences(schedule)
                .flatMap(__ -> checkStartDate(schedule))
                .flatMap(___ -> checkTickets(schedule))
                .flatMap(____ -> checkIfHallIsFree(schedule));
    }
}
