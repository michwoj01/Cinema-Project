package pl.edu.agh.ii.cinemaProject.event;

import org.springframework.context.ApplicationEvent;
import pl.edu.agh.ii.cinemaProject.model.Schedule;

public class MovieCardEvent extends ApplicationEvent {
    public MovieCardEvent(Schedule schedule) {
        super(schedule);
    }
}
