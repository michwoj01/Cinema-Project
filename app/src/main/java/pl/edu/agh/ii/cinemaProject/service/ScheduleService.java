package pl.edu.agh.ii.cinemaProject.service;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.ScheduleDao;
import pl.edu.agh.ii.cinemaProject.model.Schedule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

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

    public Either<String, Void> deleteSchedule(long scheduleId) {
        return Try.ofCallable(() -> scheduleDao.deleteById(scheduleId).block()).fold((error) -> Either.left("Unknown error"), (unused) -> Either.right(null));
    }

    private Either<String, ?> checkTickets(Schedule schedule) {
        if (schedule.getCurrently_available() < 0) {
            return Either.left("Invalid number of available tickets");
        } else {
            return Either.right(Option.none());
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
        if (!scheduleDao.getAllByCinemaHallId(schedule.getCinema_hall_id(), schedule.getStart_date()).collectList().block().isEmpty()) {
            return Either.left("There is already scheduled movie at this time and hall");
        } else {
            return Either.right(Option.none());
        }
    }

    private Either<String, ?> validateSchedule(Schedule schedule) {
        return checkEmptyReferences(schedule).flatMap(__ -> checkStartDate(schedule).flatMap(___ -> checkTickets(schedule).flatMap(____ -> checkIfHallIsFree(schedule))));
    }

    public Either<String, Schedule> createOrUpdateSchedule(Schedule schedule) {
        return validateSchedule(schedule).flatMap(
                (unused) -> Try.ofCallable(() -> scheduleDao.save(schedule).block())
                        .fold((error) -> Either.left("Unknown error"), Either::right)
        );
    }
}
