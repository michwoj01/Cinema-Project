package pl.edu.agh.ii.cinemaProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ii.cinemaProject.db.ScheduleDao;
import pl.edu.agh.ii.cinemaProject.db.UserDao;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class ReportsService {
    @Autowired
    private ScheduleDao scheduleDao;
    @Autowired
    private UserDao userDao;

    public Flux<Integer> getMoviesDisplayedByDays() {
        return scheduleDao.getMoviesDisplayedByDays();
    }

    public Flux<String> getDaysForMovies() {
        return scheduleDao.getDaysForMovies();
    }

    public Flux<Integer> getTicketsSoldByDays() {
        return scheduleDao.getTicketsSoldByDays();
    }

    public Flux<String> getDaysForTickets() {
        return scheduleDao.getDaysForTickets();
    }

    public List<String> getManagers() {
        return userDao.findAllByRoleName("moderator").collectList().block();
    }
}
